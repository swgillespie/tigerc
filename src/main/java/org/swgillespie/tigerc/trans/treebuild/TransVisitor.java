package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.ast.*;
import org.swgillespie.tigerc.common.BaseAstVisitor;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.common.Symbol;
import org.swgillespie.tigerc.semantic.RecordField;
import org.swgillespie.tigerc.semantic.RecordType;
import org.swgillespie.tigerc.trans.*;
import org.swgillespie.tigerc.trans.escape.FunctionEscapeEntry;
import org.swgillespie.tigerc.trans.escape.VariableEscapeEntry;
import org.swgillespie.tigerc.trans.ir.*;
import org.swgillespie.tigerc.trans.mips.MipsConstants;
import org.swgillespie.tigerc.trans.mips.MipsStackFrameFactory;
import org.swgillespie.tigerc.trans.mips.MipsTempFactory;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/9/15.
 */
public class TransVisitor extends BaseAstVisitor {
    private CompilationSession session;
    private TempFactory tempFactory;
    private StackFrameFactory stackFrameFactory;
    private TransTable table;
    private Stack<Level> levels;
    private Stack<TempLabel> breakLabels;

    public TransVisitor(CompilationSession session) {
        this.session = session;
        switch (session.getTarget()) {
            case MIPS:
                tempFactory = new MipsTempFactory();
                stackFrameFactory = new MipsStackFrameFactory(tempFactory);
                break;
            default:
                CompilerAssert.panic("unsupported target:" + session.getTarget());
        }
        levels = new Stack<>();
        levels.push(Level.outermost(tempFactory, stackFrameFactory));
        table = new TransTable();
        breakLabels = new Stack<>();
    }

    public void enter(FunctionDeclarationNode node) {
        FunctionEscapeEntry entry = (FunctionEscapeEntry)session.getEscapeEntryCache().get(node);
        TempLabel name = tempFactory.newNamedLabel(node.getName());
        Level thisLevel = new Level(levels.peek(),
                name,
                entry.parameterEscapeProfile(),
                stackFrameFactory);
        FunctionTransEntry transEntry = new FunctionTransEntry(thisLevel, name);
        table.insert(session.intern(node.getName()), transEntry);
        table.enterScope();
        List<Access> formals = transEntry.getLevel().getFormals();
        List<FieldDeclarationNode> parameters = node.getParameters();
        for (int i = 1; i < formals.size(); i++) {
            // the first entry is a static link, so skip that
            table.insert(session.intern(parameters.get(i - 1).getName()),
                    new VariableTransEntry(formals.get(i)));
        }
        levels.push(thisLevel);
    }

    public void exit(FunctionDeclarationNode node) {
        IRExpression functionBody = session.getIrTreeCache().get(node.getBody()).unwrapExpression(tempFactory);
        Level functionLevel = levels.pop();
        // finish the function by moving the expression result into the register designated for the return value
        IRStatement actualBody = new IRMoveTemp(new IRTemp(functionLevel.getFrame().returnValue()), functionBody);
        session.getIrTreeCache().put(node, new IRNoResultTree(actualBody));
        table.exitScope();
    }

    public void exit(VariableDeclarationNode node) {
        VariableEscapeEntry entry = (VariableEscapeEntry)session.getEscapeEntryCache().get(node);
        Access access = levels.peek().allocLocal(entry.hasEscaped());
        VariableTransEntry transEntry = new VariableTransEntry(access);
        table.insert(session.intern(node.getName()), transEntry);
    }

    public void exit(IdentifierNode node) {
        VariableTransEntry value = (VariableTransEntry)table.query(session.intern(node.getIdentifier()));
        IRExpressionTree tree = value.getAccess().simpleVar(levels.peek(), tempFactory);
        session.getIrTreeCache().put(node, tree);
    }

    public void exit(IntegerLiteralNode node) {
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRConst(node.getValue())));
    }

    public void exit(InfixExpressionNode node) {
        IRTree left = session.getIrTreeCache().get(node.getLeft());
        IRTree right = session.getIrTreeCache().get(node.getRight());
        session.getIrTreeCache().put(node,
                new IRExpressionTree(
                        new IRBinop(node.getOperator(),
                                left.unwrapExpression(tempFactory),
                                right.unwrapExpression(tempFactory))));
    }

    public void exit(ConditionalExpressionNode node) {
        // TODO - this is suboptimal. See Appel 165
        if (node.hasFalseBranch()) {
            IRConditionalTree condition = session.getIrTreeCache()
                    .get(node.getCondition())
                    .unwrapConditionalTree(tempFactory);
            IRExpression trueBranch = session.getIrTreeCache()
                    .get(node.getTrueBranch())
                    .unwrapExpression(tempFactory);
            IRExpression falseBranch = session.getIrTreeCache()
                    .get(node.getFalseBranch())
                    .unwrapExpression(tempFactory);
            TempLabel trueBranchLabel = tempFactory.newLabel();
            TempLabel falseBranchLabel = tempFactory.newLabel();
            TempLabel joinLabel = tempFactory.newLabel();
            condition.getTrueTarget().setLabel(trueBranchLabel);
            condition.getFalseTarget().setLabel(falseBranchLabel);
            TempRegister temp = tempFactory.newTemp();
            // <condition>, jumps to trueBranchLabel on true and falseBranchLabel on false
            // trueBranchLabel:
            //   r <- trueBranch
            //   jump joinLabel
            // falseBranchLabel:
            //   r <- falseBranch
            //   jump joinLabel
            // joinLabel:
            //   temp r
            IRExpression exp = new IRExpressionSequence(condition.getStatement(),
                    new IRExpressionSequence(new IRLabel(trueBranchLabel),
                            new IRExpressionSequence(new IRMoveTemp(new IRTemp(temp), trueBranch),
                                    new IRExpressionSequence(new IRJump(joinLabel),
                                            new IRExpressionSequence(new IRLabel(falseBranchLabel),
                                                    new IRExpressionSequence(new IRMoveTemp(new IRTemp(temp), falseBranch),
                                                            new IRExpressionSequence(new IRJump(joinLabel),
                                                                    new IRExpressionSequence(new IRLabel(joinLabel),
                                                                            new IRTemp(temp)))))))));
            IRTree tree = new IRExpressionTree(exp);
            session.getIrTreeCache().put(node, tree);
        } else {
            IRConditionalTree condition = session.getIrTreeCache()
                    .get(node.getCondition())
                    .unwrapConditionalTree(tempFactory);
            IRStatement trueBranch = session.getIrTreeCache()
                    .get(node.getTrueBranch())
                    .unwrapStatement(tempFactory);
            TempLabel trueBranchLabel = tempFactory.newLabel();
            TempLabel falseBranchLabel = tempFactory.newLabel();
            TempLabel joinLabel = tempFactory.newLabel();
            condition.getTrueTarget().setLabel(trueBranchLabel);
            condition.getFalseTarget().setLabel(falseBranchLabel);
            // <condition>, jumps to trueBranchLabel on true and falseBranchLabel on false
            // trueBranchLabel:
            //   <trueBranch>
            //   jump joinLabel
            // falseBranchLabel:
            //   jump joinLabel
            // joinLabel:
            //   const 0
            IRExpression exp = new IRExpressionSequence(condition.getStatement(),
                    new IRExpressionSequence(new IRLabel(trueBranchLabel),
                            new IRExpressionSequence(trueBranch,
                                    new IRExpressionSequence(new IRJump(joinLabel),
                                            new IRExpressionSequence(new IRLabel(falseBranchLabel),
                                                    new IRExpressionSequence(new IRJump(joinLabel),
                                                            new IRExpressionSequence(new IRLabel(joinLabel),
                                                                    new IRConst(0))))))));
            IRTree tree = new IRExpressionTree(exp);
            session.getIrTreeCache().put(node, tree);
        }
    }

    public void enter(WhileExpressionNode node) {
        TempLabel breakLabel = tempFactory.newLabel();
        breakLabels.push(breakLabel);
    }

    public void exit(WhileExpressionNode node) {
        IRConditionalTree condition = session.getIrTreeCache()
                .get(node.getCondition())
                .unwrapConditionalTree(tempFactory);
        IRStatement body = session.getIrTreeCache()
                .get(node.getBody())
                .unwrapStatement(tempFactory);
        TempLabel conditionLabel = tempFactory.newLabel();
        TempLabel bodyLabel = tempFactory.newLabel();
        TempLabel exitLabel = breakLabels.pop();
        condition.getTrueTarget().setLabel(bodyLabel);
        condition.getFalseTarget().setLabel(exitLabel);
        // conditionLabel:
        //   <condition>, jumps to bodyLabel on true and exitLabel on false
        // bodyLabel:
        //   <body>
        //   jump conditionLabel
        // exitLabel:
        //   const 0
        IRExpression exp = new IRExpressionSequence(new IRLabel(conditionLabel),
                new IRExpressionSequence(condition.getStatement(),
                        new IRExpressionSequence(new IRLabel(bodyLabel),
                                new IRExpressionSequence(body,
                                        new IRExpressionSequence(new IRJump(conditionLabel),
                                                new IRExpressionSequence(new IRLabel(exitLabel),
                                                        new IRConst(0)))))));
        IRTree tree = new IRExpressionTree(exp);
        session.getIrTreeCache().put(node, tree);
    }

    public void exit(BreakNode node) {
        // break jumps to the innermost loop's exit label.
        IRExpression exp = new IRExpressionSequence(new IRJump(breakLabels.peek()), new IRConst(0));
        session.getIrTreeCache().put(node, new IRExpressionTree(exp));
    }

    public void exit(FieldAccessNode node) {
        IRExpression base = session.getIrTreeCache().get(node.getBase()).unwrapExpression(tempFactory);
        // base is an expression that returns a pointer to a record. We need to calculate the offset of
        // this field in the record. The first field is at 0, the second at wordSize, the third at
        // 2 * wordSize, etc.
        Symbol thisField = session.intern(node.getFieldName());
        int offset = 0;
        RecordType ty = (RecordType)session.getTypeCache().get(node.getBase());
        for (RecordField field : ty.getFields()) {
            if (field.name == thisField) {
                break;
            }
            offset += MipsConstants.wordSize;
        }
        // expr = mem [record + offset]
        IRExpression basePlusOffset = new IRMem(new IRBinop(InfixOperator.Plus, base,
                new IRConst(offset)), MipsConstants.wordSize);
        session.getIrTreeCache().put(node, new IRExpressionTree(basePlusOffset));
    }

    public void exit(ArrayAccessNode node) {
        IRExpression base = session.getIrTreeCache().get(node.getBase()).unwrapExpression(tempFactory);
        IRExpression index = session.getIrTreeCache().get(node.getIndex()).unwrapExpression(tempFactory);
        // base is a pointer to an array, and index is an integer representing an index into the array.
        // expr = mem [base + (index * wordSize)]
        IRExpression offset = new IRMem(new IRBinop(InfixOperator.Plus, base,
                        new IRBinop(InfixOperator.Mul, index,
                                new IRConst(MipsConstants.wordSize))), MipsConstants.wordSize);
        session.getIrTreeCache().put(node, new IRExpressionTree(offset));
    }

    public void exit(NilNode node) {
        // the dreaded null pointer.
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRConst(0)));
    }

    public void exit(NegationExpressionNode node) {
        // negation can be implemented as 0 - expression.
        IRExpression base = session.getIrTreeCache()
                .get(node.getNegatedExpression())
                .unwrapExpression(tempFactory);
        IRExpression exp = new IRBinop(InfixOperator.Minus, new IRConst(0), base);
        session.getIrTreeCache().put(node, new IRExpressionTree(exp));
    }

    public void exit(ArrayCreationExpressionNode node) {
        // TODO
    }

    public void exit(RecordCreationExpressionNode node) {
        // TODO
    }

    public void exit(ForExpressionNode node) {
        // TODO
    }

    public void exit(SequenceExpressionNode node) {
        List<IRTree> exprs = node.getSequence()
                .stream()
                .map(i -> session.getIrTreeCache().get(i))
                .collect(Collectors.toList());
        IRExpression last = exprs.get(exprs.size() - 1).unwrapExpression(tempFactory);
        IRExpression total = last;
        for (int i = exprs.size() - 2; i >= 0; i--) {
            total = new IRExpressionSequence(exprs.get(i).unwrapStatement(tempFactory), total);
        }
        session.getIrTreeCache().put(node, new IRExpressionTree(total));
    }

    public void exit(CallExpressionNode node) {
        FunctionTransEntry func = (FunctionTransEntry)table.query(session.intern(node.getFunctionName()));
        List<IRExpression> parameters = node.getParameters()
                .stream()
                .map(i -> session.getIrTreeCache().get(i))
                .map(i -> i.unwrapExpression(tempFactory))
                .collect(Collectors.toList());
        IRExpression callExpr = new IRCall(new IRName(func.getLabel()), parameters);
        session.getIrTreeCache().put(node, new IRExpressionTree(callExpr));
    }
}
