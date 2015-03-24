package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.ast.*;
import org.swgillespie.tigerc.common.BaseAstVisitor;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.common.Symbol;
import org.swgillespie.tigerc.semantic.*;
import org.swgillespie.tigerc.trans.*;
import org.swgillespie.tigerc.trans.escape.FunctionEscapeEntry;
import org.swgillespie.tigerc.trans.escape.VariableEscapeEntry;
import org.swgillespie.tigerc.trans.ir.*;

import java.util.Arrays;
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
    private TransFragments fragments;

    public TransVisitor(CompilationSession session) {
        this.session = session;
        tempFactory = session.getTempFactory();
        stackFrameFactory = session.getStackFrameFactory();
        levels = new Stack<>();
        levels.push(Level.outermost(tempFactory, stackFrameFactory));
        table = new TransTable();
        breakLabels = new Stack<>();
        fragments = new TransFragments();
        this.initializeTable();
    }

    private void initializeTable() {
        this.table.insert(session.intern("print"),
                new FunctionTransEntry(levels.peek(), tempFactory.print()));
        this.table.insert(session.intern("flush"),
                new FunctionTransEntry(levels.peek(), tempFactory.flush()));
        this.table.insert(session.intern("getchar"),
                new FunctionTransEntry(levels.peek(), tempFactory.getchar()));
        this.table.insert(session.intern("ord"),
                new FunctionTransEntry(levels.peek(), tempFactory.ord()));
        this.table.insert(session.intern("chr"),
                new FunctionTransEntry(levels.peek(), tempFactory.chr()));
        this.table.insert(session.intern("size"),
                new FunctionTransEntry(levels.peek(), tempFactory.size()));
        this.table.insert(session.intern("substring"),
                new FunctionTransEntry(levels.peek(), tempFactory.substring()));
        this.table.insert(session.intern("concat"),
                new FunctionTransEntry(levels.peek(), tempFactory.concat()));
        this.table.insert(session.intern("not"),
                new FunctionTransEntry(levels.peek(), tempFactory.not()));
        this.table.insert(session.intern("exit"),
                new FunctionTransEntry(levels.peek(), tempFactory.exit()));

    }

    public TransFragments getFragments() {
        return fragments;
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
        // moving the expression result into the register designated for the return value
        IRStatement bodyWithRet = new IRMoveTemp(new IRTemp(functionLevel.getFrame().returnValue()), functionBody);
        // decorate the function with its pre/postlude that does the view shift and saves/loads callee-saved
        // registers
        IRStatement actualBody = functionLevel.getFrame().procEntryExit(bodyWithRet);
        Fragment procFragment = Fragment.procFragment(actualBody, functionLevel.getFrame());
        fragments.add(procFragment);
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRConst(0)));
        table.exitScope();
    }

    public void exit(VariableDeclarationNode node) {
        VariableEscapeEntry entry = (VariableEscapeEntry)session.getEscapeEntryCache().get(node);
        Access access = levels.peek().allocLocal(entry.hasEscaped());
        VariableTransEntry transEntry = new VariableTransEntry(access);
        table.insert(session.intern(node.getName()), transEntry);
        session.getIrTreeCache().put(node, session.getIrTreeCache().get(node.getInitializer()));
    }

    public void exit(TypeDeclarationNode node) {
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRConst(0)));
    }

    public void exit(LetExpressionNode node) {
        // first - the final element in the list of expressions is the resulting value.
        List<ExpressionNode> body = node.getBody();
        ExpressionNode last = body.get(body.size() - 1);
        IRExpression finalExp = session.getIrTreeCache().get(last).unwrapExpression(tempFactory);
        IRExpression compositeExp = finalExp;
        for (int i = body.size() - 2; i >= 0; i--) {
            IRStatement expr = session.getIrTreeCache().get(body.get(i)).unwrapStatement(tempFactory);
            compositeExp = new IRExpressionSequence(expr, compositeExp);
        }

        for (DeclarationNode dec : node.getDeclarations()) {
            if (dec instanceof VariableDeclarationNode) {
                IRExpression initializer = session.getIrTreeCache().get(dec).unwrapExpression(tempFactory);
                VariableTransEntry entry = (VariableTransEntry)table.query(session.intern(dec.getName()));
                IRExpression entryLocation = entry.getAccess().simpleVar(levels.peek(), tempFactory).unwrapExpression(tempFactory);
                IRStatement creation = new IRMoveMem(entryLocation, stackFrameFactory.wordSize(), initializer);
                compositeExp = new IRExpressionSequence(creation, compositeExp);
            }
        }
        session.getIrTreeCache().put(node, new IRExpressionTree(compositeExp));
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
        IRExpression lhs = session.getIrTreeCache().get(node.getLeft()).unwrapExpression(tempFactory);
        IRExpression rhs = session.getIrTreeCache().get(node.getRight()).unwrapExpression(tempFactory);
        IRExpression result = null;
        switch (node.getOperator()) {
            case Plus:
            case Minus:
            case Mul:
            case Div:
            case And:
            case Or:
                // semant has guaranteed that the lhs and rhs are integers
                result = new IRBinop(node.getOperator(), lhs, rhs);
                break;
            case Eq:
            case Neq:
                // these could be anything, but semant guarantees that
                // lhs and rhs have the same type.
                // in reality, strings are the only case we care about.
                // records and arrays are just pointers and pointer comparison
                // is the same as int comparison.
                Type lhsType = session.getTypeCache().get(node.getLeft());
                if (lhsType instanceof StringType) {
                    // they are both strings. we need to emit a call to the runtime
                    // string comparison function.
                    IRExpression compResult = new IRCall(new IRName(tempFactory.strcmp()), Arrays.asList(lhs, rhs));
                    result = new IRBinop(node.getOperator(), compResult, new IRConst(0));
                } else {
                    // they are integers, records, or arrays. no matter what, we need
                    // to do an integer comparison.
                    result = new IRBinop(node.getOperator(), lhs, rhs);
                }
                break;
            case LessThan:
            case Leq:
            case GreaterThan:
            case Geq:
                // these could be either ints or strings.
                // if they are ints, it's a normal comparison. if they
                // are strings, it's a lexicographic comparison.
                Type left = session.getTypeCache().get(node.getLeft());
                if (left instanceof IntegerType) {
                    result = new IRBinop(node.getOperator(), lhs, rhs);
                } else {
                    IRExpression compResult = new IRCall(new IRName(tempFactory.strcmp()), Arrays.asList(lhs, rhs));
                    result = new IRBinop(node.getOperator(), compResult, new IRConst(0));
                }
                break;
        }
        session.getIrTreeCache().put(node, new IRExpressionTree(result));
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
            offset += stackFrameFactory.wordSize();
        }
        // expr = mem [record + offset]
        IRExpression basePlusOffset = new IRMem(new IRBinop(InfixOperator.Plus, base,
                new IRConst(offset)), stackFrameFactory.wordSize());
        session.getIrTreeCache().put(node, new IRExpressionTree(basePlusOffset));
    }

    public void exit(ArrayAccessNode node) {
        IRExpression base = session.getIrTreeCache().get(node.getBase()).unwrapExpression(tempFactory);
        IRExpression index = session.getIrTreeCache().get(node.getIndex()).unwrapExpression(tempFactory);
        // base is a pointer to an array, and index is an integer representing an index into the array.
        // expr = mem [base + (index * wordSize)]
        IRExpression offset = new IRMem(new IRBinop(InfixOperator.Plus, base,
                        new IRBinop(InfixOperator.Mul, index,
                                new IRConst(stackFrameFactory.wordSize()))), stackFrameFactory.wordSize());
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
        IRExpression initializer = session.getIrTreeCache().get(node.getInitializer()).unwrapExpression(tempFactory);
        IRExpression length = session.getIrTreeCache().get(node.getLengthExpression()).unwrapExpression(tempFactory);
        TempRegister reg = tempFactory.newTemp();
        TempRegister init = tempFactory.newTemp();
        TempRegister lenReg = tempFactory.newTemp();
        // lenReg <- length
        IRStatement lengthInitializer = new IRMoveTemp(new IRTemp(lenReg), length);
        // reg <- malloc(lenReg * wordSize)
        IRStatement malloc = new IRMoveTemp(
                new IRTemp(reg),
                new IRCall(new IRName(tempFactory.malloc()),
                        Arrays.asList(new IRBinop(InfixOperator.Mul, new IRTemp(lenReg),
                                new IRConst(stackFrameFactory.wordSize())))));
        // init <- initializer
        IRStatement evalInitializer = new IRMoveTemp(new IRTemp(init), initializer);
        // memset(reg, init) - reg is a pointer to a newly allocated array, init is a value
        // that will be used to populate the array
        IRStatement memset = new IREvalAndDiscard(new IRCall(new IRName(tempFactory.memset()),
                Arrays.asList(new IRTemp(reg), new IRTemp(init), new IRTemp(lenReg))));
        // putting it all together and yielding reg at the end
        // TODO the initializer is evaluated before the length of the array. is that correct?
        IRExpression wholeThing = new IRExpressionSequence(lengthInitializer,
                new IRExpressionSequence(evalInitializer,
                        new IRExpressionSequence(malloc,
                                new IRExpressionSequence(memset, new IRTemp(reg)))));
        session.getIrTreeCache().put(node, new IRExpressionTree(wholeThing));
    }

    public void exit(RecordCreationExpressionNode node) {
        RecordType ty = (RecordType)session.getTypeCache().get(node);
        TempRegister reg = tempFactory.newTemp();
        IRStatement init = null;
        // building backwards!
        List<RecordField> fields = ty.getFields();
        List<FieldCreationNode> creationNode = node.getFields();
        for (int i = fields.size() - 1; i >= 0; i--) {
            IRExpression initExpr = session.getIrTreeCache().get(creationNode.get(i)).unwrapExpression(tempFactory);
            IRStatement fieldCreationStatement = new IRMoveMem(
                    new IRMem(
                            new IRBinop(InfixOperator.Plus, new IRTemp(reg),
                                    new IRConst(i * stackFrameFactory.wordSize())),
                            stackFrameFactory.wordSize()),
                        stackFrameFactory.wordSize(),
                    initExpr);
            if (init == null) {
                init = fieldCreationStatement;
            } else {
                init = new IRSeq(fieldCreationStatement, init);
            }
        }
        IRStatement malloc = new IRSeq(
                new IRMoveTemp(
                        new IRTemp(reg),
                        new IRCall(new IRName(tempFactory.malloc()), Arrays.asList(
                                new IRConst(fields.size() * stackFrameFactory.wordSize())))), init);
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRExpressionSequence(malloc, new IRTemp(reg))));
    }

    public void exit(FieldCreationNode node) {
        session.getIrTreeCache().put(node, session.getIrTreeCache().get(node.getFieldValue()));
    }

    public void exit(ForExpressionNode node) {
        // TODO - transform the for loop into a while loop and call exit(WhileExpressionNode)
        CompilerAssert.panic("not implemented yet");
    }

    public void exit(SequenceExpressionNode node) {
        List<IRTree> exprs = node.getSequence()
                .stream()
                .map(i -> session.getIrTreeCache().get(i))
                .collect(Collectors.toList());
        IRExpression last = exprs.get(exprs.size() - 1).unwrapExpression(tempFactory);
        for (int i = exprs.size() - 2; i >= 0; i--) {
            last = new IRExpressionSequence(exprs.get(i).unwrapStatement(tempFactory), last);
        }
        session.getIrTreeCache().put(node, new IRExpressionTree(last));
    }

    public void exit(CallExpressionNode node) {
        FunctionTransEntry func = (FunctionTransEntry)table.query(session.intern(node.getFunctionName()));
        List<IRExpression> parameters = node.getParameters()
                .stream()
                .map(i -> session.getIrTreeCache().get(i))
                .map(i -> i.unwrapExpression(tempFactory))
                .collect(Collectors.toList());
        // the first parameter of the function is always the static link.
        StackFrame thisFrame = this.levels.peek().getFrame();
        IRExpression staticLink = this.levels.peek()
                .getFormals()
                .get(0)
                .getAccess()
                .toExpression(new IRTemp(thisFrame.framePointer()), tempFactory);
        parameters.add(0, staticLink);
        Type functionReturnTy = session.getTypeCache().get(node);
        if (functionReturnTy instanceof VoidType) {
            // if the function has void return type, we can safely discard the return value.
            IRStatement call = new IREvalAndDiscard(new IRCall(new IRName(func.getLabel()), parameters));
            session.getIrTreeCache().put(node, new IRNoResultTree(call));
        } else {
            IRExpression callExpr = new IRCall(new IRName(func.getLabel()), parameters);
            session.getIrTreeCache().put(node, new IRExpressionTree(callExpr));
        }
    }

    public void exit(StringLiteralNode node) {
        TempLabel label = tempFactory.newLabel();
        Fragment strFragment = Fragment.stringFragment(label, node.getValue());
        fragments.add(strFragment);
        session.getIrTreeCache().put(node, new IRExpressionTree(new IRName(label)));
    }

    public ProcFragment transToplevel(AstNode toplevelNode) {
        CompilerAssert.check(levels.size() == 1, "transToplevel called when not at toplevel?");
        IRTree tree = session.getIrTreeCache().get(toplevelNode);
        TempRegister result = tempFactory.newTemp();
        IRStatement body = new IRSeq(new IRMoveTemp(new IRTemp(result), tree.unwrapExpression(tempFactory)),
                new IREvalAndDiscard(new IRCall(new IRName(tempFactory.exit()),
                        Arrays.asList(new IRTemp(result)))));
        return new ProcFragment(body, levels.pop().getFrame());
    }

}
