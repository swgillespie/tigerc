package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public final class IRConditionalTree extends IRTree {
    private UpdateableTempLabel trueTarget;
    private UpdateableTempLabel falseTarget;
    private IRStatement statement;

    public IRConditionalTree(UpdateableTempLabel trueTarget, UpdateableTempLabel falseTarget, IRStatement statement) {
        this.trueTarget = trueTarget;
        this.falseTarget = falseTarget;
        this.statement = statement;
    }

    public UpdateableTempLabel getTrueTarget() {
        return trueTarget;
    }

    public UpdateableTempLabel getFalseTarget() {
        return falseTarget;
    }

    public IRStatement getStatement() {
        return statement;
    }

    /**
     * This function "unwraps" an IR tree into an expression. This is done three
     * different ways, depending on what is contained in this IRTree instance.
     *
     * If this IRTree is an IRExpressionTree, it trivially returns the expression
     * within the IRExpressionTree. If this IRTree is an IRNoResultTree, then it
     * returns an expression sequence that executes the statement within it and
     * returns the value 0. The interesting case is when this IRTree is an IRConditionalTree,
     * as we then have to build up the branching mechanism. We create a new temp register (reg) and two
     * temp labels (trueBranch, falseBranch) for the two branches of the conditional and use them to
     * build an expression that looks a little like this:
     *
     * reg <- 1
     * [the conditional tree's statement]
     * falseBranch:
     *   reg <- 0
     * trueBranch:
     *   reg
     *
     * That is, the register is set to 1 and the conditional tree's statement is executed.
     * If the statement jumps to the true branch, reg contains 1 and so the resulting expression
     * evaluates to 1. If the statement jumps to the false branch, reg gets set to 0 and the resulting
     * expression evaluates to 0.
     * @param factory The temp factory to use to create the temporary values
     * @return An expression that, when executed, yields the correct value of the expression while still performing
     * side effects.
     */
    @Override
    public IRExpression unwrapExpression(TempFactory factory) {
        TempRegister reg = factory.newTemp();
        TempLabel trueBranch = factory.newLabel();
        TempLabel falseBranch = factory.newLabel();
        if (trueTarget != null) {
            trueTarget.setLabel(trueBranch);
        }
        if (falseTarget != null) {
            falseTarget.setLabel(falseBranch);
        }
        return new IRExpressionSequence(new IRMoveTemp(new IRTemp(reg), new IRConst(1)),
                new IRExpressionSequence(statement,
                        new IRExpressionSequence(new IRLabel(falseBranch),
                                new IRExpressionSequence(new IRMoveTemp(new IRTemp(reg), new IRConst(0)),
                                        new IRExpressionSequence(new IRLabel(trueBranch), new IRTemp(reg))))));

    }

    @Override
    public IRStatement unwrapStatement(TempFactory factory) {
        IRExpression expr = this.unwrapExpression(factory);
        return new IREvalAndDiscard(expr);
    }

    @Override
    public IRConditionalTree unwrapConditionalTree(TempFactory factory) {
        CompilerAssert.panic("this should never happen in a well-typed program");
        return null;
    }

    @Override
    public String toString() {
        return "IRConditionalTree{" +
                "trueTarget=" + trueTarget +
                ", falseTarget=" + falseTarget +
                ", statement=" + statement +
                '}';
    }
}
