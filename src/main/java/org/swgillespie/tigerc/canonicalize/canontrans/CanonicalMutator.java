package org.swgillespie.tigerc.canonicalize.canontrans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.swgillespie.tigerc.canonicalize.IRMutator;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempRegister;
import org.swgillespie.tigerc.trans.ir.*;

/**
 * Created by sean on 3/12/15.
 */
public class CanonicalMutator extends IRMutator {
    private TempFactory tempFactory;

    public CanonicalMutator(CompilationSession session) {
        this.tempFactory = session.getTempFactory();
    }

    private static boolean isNop(IRStatement statement) {
        return (statement instanceof IREvalAndDiscard)
                && (((IREvalAndDiscard) statement).getExpr() instanceof IRConst);
    }

    private static boolean commute(IRStatement statement, IRExpression expr) {
        return isNop(statement) || (expr instanceof IRName) || (expr instanceof IRConst);
    }

    @Override
    public IRExpression visitExpressionSequence(IRExpressionSequence node) {
        node.setStmt(this.visitStatement(node.getStmt()));
        node.setResult(this.visitExpression(node.getResult()));
        // rewriting rule 1:
        // ESEQ(s1, ESEQ(s2, e)) -> ESEQ(SEQ(s1, s2) e)
        // this "lifts" a statement higher up the tree and removes an ESEQ
        IRStatement stmt = node.getStmt();
        if (node.getResult() instanceof IRExpressionSequence) {
            IRExpressionSequence subExp = (IRExpressionSequence)node.getResult();
            stmt = new IRSeq(stmt, subExp.getStmt());
            return new IRExpressionSequence(stmt, subExp.getResult());
        }
        return node;
    }

    @Override
    public IRExpression visitBinop(IRBinop node) {
        node.setLeft(this.visitExpression(node.getLeft()));
        node.setRight(this.visitExpression(node.getRight()));
        // rewriting rule 2:
        // BINOP(op, ESEQ(s, e1) e2) -> ESEQ(s, BINOP(op, e1, e2))
        if (node.getLeft() instanceof IRExpressionSequence) {
            IRExpressionSequence leftExp = (IRExpressionSequence)node.getLeft();
            return new IRExpressionSequence(leftExp.getStmt(),
                    new IRBinop(node.getOp(), leftExp.getResult(), node.getRight()));
        }
        // rewriting rule 3:
        // BINOP(op, e1, ESEQ(s, e2)) -> ESEQ(MOVE(TEMP(t), e1), ESEQ(s, BINOP(op, TEMP(t), e2)))
        // this lifts an ESEQ out of a binop.
        if (node.getRight() instanceof IRExpressionSequence) {
            IRExpressionSequence rightExp = (IRExpressionSequence)node.getRight();
            if (commute(rightExp.getStmt(), node.getLeft())) {
                // it is safe to switch the evaluation order of s and e1.
                // BINOP(op, e1, ESEQ(s, e2)) -> ESEQ(s, BINOP(op, e1, e2))
                return this.visitExpressionSequence(new IRExpressionSequence(rightExp.getStmt(),
                        new IRBinop(node.getOp(), node.getLeft(), rightExp.getResult())));
            }
            // if it is not safe to switch the evaluation order of s and e1, we need to
            // create a new temp to save e1 before executing s and, finally, the binary op.
            TempRegister t = tempFactory.newTemp();
            IRStatement move = new IRMoveTemp(new IRTemp(t), node.getLeft());
            IRExpression expr = new IRExpressionSequence(rightExp.getStmt(),
                    new IRBinop(node.getOp(), new IRTemp(t), rightExp.getResult()));
            return new IRExpressionSequence(move, expr);
        }
        return node;
    }

    @Override
    public IRExpression visitMem(IRMem node) {
        node.setAddress(this.visitExpression(node.getAddress()));
        if (node.getAddress() instanceof IRExpressionSequence) {
            // rewriting rule 2.1
            // MEM(ESEQ(s, e)) -> ESEQ(s, MEM(e1))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getAddress();
            return new IRExpressionSequence(eseq.getStmt(),
                    new IRMem(eseq.getResult(), node.getWordSize()));
        }
        return node;
    }

    @Override
    public IRStatement visitMoveTemp(IRMoveTemp node) {
        node.setValue(this.visitExpression(node.getValue()));
        if (node.getValue() instanceof IRExpressionSequence) {
            // MOVE(TEMP t, ESEQ(s1, e1)) -> SEQ(s1, MOVE(TEMP t, e1))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getValue();
            return new IRSeq(eseq.getStmt(), new IRMoveTemp(node.getDestination(), eseq.getResult()));
        }
        return node;
    }

    @Override
    public IRStatement visitJump(IRJump node) {
        node.setTarget(this.visitExpression(node.getTarget()));
        if (node.getTarget() instanceof IRExpressionSequence) {
            // rewriting rule 2.2
            // JUMP(ESEQ(s, e1)) -> SEQ(s, JUMP(e1))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getTarget();
            return new IRSeq(eseq.getStmt(),
                    new IRJump(eseq.getResult(), node.getPossibleTargetLabels()));
        }
        return node;
    }

    @Override
    public IRStatement visitConditionalJump(IRConditionalJump node) {
        node.setFirstExpr(this.visitExpression(node.getFirstExpr()));
        node.setSecondExpr(this.visitExpression(node.getSecondExpr()));
        if (node.getFirstExpr() instanceof IRExpressionSequence) {
            // rewriting rule 2.3
            // CJUMP(op, ESEQ(s, e1), e2, l1, l2) -> SEQ(s, CJUMP(op, e1, e2, l1, l2))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getFirstExpr();
            return new IRSeq(eseq.getStmt(),
                    new IRConditionalJump(eseq.getResult(),
                            node.getSecondExpr(),
                            node.getRelationalOp(),
                            node.getTrueTarget(),
                            node.getFalseTarget()));
        }
        if (node.getSecondExpr() instanceof IRExpressionSequence) {
            // rewriting rule 4.1
            // CJUMP(op, e1, ESEQ(s, e2), l1, l2) -> SEQ(s, CJUMP(op, e1, e2, l1, l2))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getSecondExpr();
            if (commute(eseq.getStmt(), node.getFirstExpr())) {
                // swap the execution order of s and e1 since they commute
                return new IRSeq(eseq.getStmt(),
                        new IRConditionalJump(eseq.getResult(),
                                node.getSecondExpr(),
                                node.getRelationalOp(),
                                node.getTrueTarget(),
                                node.getFalseTarget()));
            } else {
                // otherwise, we have to save e1 before executing s
                TempRegister t = tempFactory.newTemp();
                IRStatement move = new IRMoveTemp(new IRTemp(t), node.getFirstExpr());
                IRStatement branch = new IRConditionalJump(new IRTemp(t),
                        eseq.getResult(),
                        node.getRelationalOp(),
                        node.getTrueTarget(),
                        node.getFalseTarget());
                return new IRSeq(move, new IRSeq(eseq.getStmt(), branch));
            }
        }
        return node;
    }

    @Override
    public IRExpression visitCall(IRCall node) {
        node.setFunction(this.visitExpression(node.getFunction()));
        node.setArguments(node.getArguments()
                .stream()
                .map(this::visitExpression)
                .collect(Collectors.toList()));

        List<IRExpression> args = node.getArguments();
        List<IRExpression> newArgs = new ArrayList<>();
        IRStatement liftedArguments = null;
        if (args.stream().anyMatch(t -> t instanceof IRExpressionSequence)) {
            // there's a statement that needs to be lifted outside of
            // this call. we may need to lift some expression evaluations
            // out of this call as well, depending on whether or not they commute.
            // for simplicity's sake, if there's an ESEQ as a parameter to a function,
            // we lift everything out of the call. The optimization passes later on can
            // sort out the mess we make here.
            for (IRExpression expr : args) {
                if (expr instanceof IRExpressionSequence) {
                    IRExpressionSequence eseq = (IRExpressionSequence)expr;
                    TempRegister reg = tempFactory.newTemp();
                    // lift the statement, then the expression
                    if (liftedArguments == null) {
                        liftedArguments = new IRSeq(eseq.getStmt(),
                                new IRMoveTemp(new IRTemp(reg), eseq.getResult()));
                    } else {
                        liftedArguments = new IRSeq(eseq.getStmt(),
                                new IRSeq(liftedArguments,
                                        new IRMoveTemp(new IRTemp(reg), eseq.getResult())));
                    }
                    newArgs.add(new IRTemp(reg));
                } else {
                    // otherwise, lift the expression.
                    TempRegister reg = tempFactory.newTemp();
                    if (liftedArguments == null) {
                        liftedArguments = new IRMoveTemp(new IRTemp(reg), expr);
                    } else {
                        liftedArguments = new IRSeq(liftedArguments, new IRMoveTemp(new IRTemp(reg), expr));
                    }
                    newArgs.add(new IRTemp(reg));
                }
            }
        } else {
            // even if there are no ESEQs, we still need to lift calls out. this is because most, if not all,
            // architectures have a designated return value register. If we have Tiger code like:
            //
            // let function add(x: int, y: int): int = x + y in
            //   add(add(2, 2), add(4, 4))
            //
            // we run into problems, since the return value of add(2, 2) is in the return register
            // when add(4, 4) gets executed, and the return value of add(2, 2) gets clobbered.
            // to fix this issue, we need to lift calls out of the parameters of a call node.
            // The above code snippet gets transformed into IR like:
            //
            // %temp1 <- add(2, 2)
            // %temp2 <- add(4, 4)
            // add(%temp1, %temp2)
            //
            // which works out nicely.
            for (IRExpression arg : args) {
                if (arg instanceof IRCall) {
                    TempRegister reg = tempFactory.newTemp();
                    if (liftedArguments == null) {
                        liftedArguments = new IRMoveTemp(new IRTemp(reg), arg);
                    } else {
                        liftedArguments = new IRSeq(liftedArguments, new IRMoveTemp(new IRTemp(reg), arg));
                    }
                    newArgs.add(new IRTemp(reg));
                } else {
                    newArgs.add(arg);
                }
            }
        }
        if (liftedArguments != null) {
            return new IRExpressionSequence(liftedArguments, new IRCall(node.getFunction(), newArgs));
        } else {
            return new IRCall(node.getFunction(), newArgs);
        }
    }

    @Override
    public IRStatement visitEvalAndDiscard(IREvalAndDiscard node) {
        node.setExpr(this.visitExpression(node.getExpr()));
        if (node.getExpr() instanceof IRExpressionSequence) {
            // given that e1 has no eseqs,
            // EXP(ESEQ(s1, e1)) -> SEQ(s1, EXP(e1))
            IRExpressionSequence eseq = (IRExpressionSequence)node.getExpr();
            return new IRSeq(eseq.getStmt(), new IREvalAndDiscard(eseq.getResult()));
        }
        return node;
    }

    private static IRStatement seq(IRStatement first, IRStatement last) {
        if (isNop(first)) return last;
        if (isNop(last)) return first;
        return new IRSeq(first, last);
    }

    public IRStatement linearize(IRStatement source) {
        if (source instanceof IRSeq) {
            IRSeq seq = (IRSeq)source;
            seq.setFirst(linearize(seq.getFirst()));
            if (seq.getFirst() instanceof IRSeq) {
                /*
                turns
                               seq
                              / \
                             /   \
                           seq   s3
                            /\
                           /  \
                          s1  s2

                 into
                              seq
                              / \
                             /   \
                            s1  seq
                                / \
                               /   \
                              s2   s3
                 */
                IRSeq childSeq = (IRSeq)seq.getFirst();
                return new IRSeq(childSeq.getFirst(), linearize(new IRSeq(childSeq.getSecond(), seq.getSecond())));
            }
        }
        // this isn't a seq and is the tail of the list.
        return source;
    }

    /*
    private List<StatementExprPair> reorder(List<ExpressionReference> refs) {
        List<IRStatement> statements = new ArrayList<>();
        List<IRExpression> expressions = new ArrayList<>();

        if (refs.size() == 0) {
            return Arrays.asList(new StatementExprPair(new IREvalAndDiscard(new IRConst(0)), null));
        }

        for (int i = 0; i < refs.size(); i++) {
            ExpressionReference ref = refs.get(i);
            if (ref.getRef() instanceof IRCall) {
                TempRegister reg = tempFactory.newTemp();
                IRExpression transformedCall = new IRExpressionSequence(
                        new IRMoveTemp(new IRTemp(reg), ref.getRef()),
                        new IRTemp(reg));
                List<ExpressionReference> clone = refs.subList(i + 1, refs.size() - 1);
                clone.add(0, new ExpressionReference(transformedCall));
                return reorder(clone);
            }
        }
    }

    private IRStatement doStm(IRStatement statement) {
        if (statement instanceof IRSeq) {
            IRSeq seq = (IRSeq)statement;
            return seq(doStm(seq.getFirst()), doStm(seq.getSecond()));
        }
        if (statement instanceof IRJump) {
            List<ExpressionReference> refs = new ArrayList<>();
            refs.add(new ExpressionReference(((IRJump) statement).getTarget()));
            return seq(reorder(refs), statement);
        }
        if (statement instanceof IRConditionalJump) {
            IRConditionalJump jmp = (IRConditionalJump)statement;
            List<ExpressionReference> refs = Arrays.asList(
                    new ExpressionReference(jmp.getFirstExpr()),
                    new ExpressionReference(jmp.getSecondExpr()));

            return seq(reorder(refs), statement);
        }
        if (statement instanceof IRMoveMem) {
            IRMoveMem mov = (IRMoveMem)statement;
            if (mov.getAddressExpression() instanceof IRMem) {
                IRMem mem = (IRMem)mov.getAddressExpression();
                List<ExpressionReference> refs = Arrays.asList(
                        new ExpressionReference(mem.getAddress()),
                        new ExpressionReference(mov.getValue()));
                return seq(reorder(refs), statement);
            }
            if (mov.getAddressExpression() instanceof IRExpressionSequence) {
                IRExpressionSequence eseq = (IRExpressionSequence)mov.getAddressExpression();
                IRStatement firstStm = eseq.getStmt();
                mov.setAddressExpression(eseq.getResult());
                return doStm(new IRSeq(firstStm, statement));
            }
            CompilerAssert.panic("if we get here, something went wrong in trans");
            return null;
        }
        if (statement instanceof IRMoveTemp) {
            IRMoveTemp mov = (IRMoveTemp)statement;
            if (mov.getValue() instanceof IRCall) {
                List<ExpressionReference> refs = ((IRCall) mov.getValue()).getArguments()
                        .stream()
                        .map(ExpressionReference::new)
                        .collect(Collectors.toList());
                return seq(reorder(refs), statement);
            }
            List<ExpressionReference> refs = Arrays.asList(new ExpressionReference(mov.getValue()));
            return seq(reorder(refs), statement);
        }
        if (statement instanceof IREvalAndDiscard) {
            IREvalAndDiscard exp = (IREvalAndDiscard)statement;
            if (exp.getExpr() instanceof IRCall) {
                List<ExpressionReference> refs = ((IRCall) exp.getExpr()).getArguments()
                        .stream()
                        .map(ExpressionReference::new)
                        .collect(Collectors.toList());
                return seq(reorder(refs), statement);
            }
            List<ExpressionReference> refs = Arrays.asList(new ExpressionReference(exp.getExpr()));
            return seq(reorder(refs), statement);
        }
        return statement;
    }

    private StatementExprPair doExp(IRExpression expr) {
        if (expr instanceof IRBinop) {
            List<ExpressionReference> refs = new ArrayList<>();
            IRBinop binop = (IRBinop)expr;
            refs.add(new ExpressionReference(binop.getLeft()));
            refs.add(new ExpressionReference(binop.getRight()));
            refs.add(new ExpressionReference(expr));
            return new StatementExprPair(reorder(refs), expr);
        }
        if (expr instanceof IRMem) {
            List<ExpressionReference> refs = new ArrayList<>();
            refs.add(new ExpressionReference(((IRMem) expr).getAddress()));
            return new StatementExprPair(reorder(refs), expr);
        }
        if (expr instanceof IRExpressionSequence) {
            IRExpressionSequence eseq = (IRExpressionSequence)expr;
            StatementExprPair pair = doExp(eseq.getResult());
            return new StatementExprPair(seq(doStm(eseq.getStmt()), pair.getStatement()), pair.getExpression());
        }
        if (expr instanceof IRCall) {
            List<ExpressionReference> refs = ((IRCall) expr).getArguments()
                    .stream()
                    .map(ExpressionReference::new)
                    .collect(Collectors.toList());
            return new StatementExprPair(reorder(refs), expr);
        }
        return new StatementExprPair(reorder(new ArrayList<>()), expr);
    }
    */
}
