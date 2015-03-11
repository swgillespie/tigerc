package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempFactory;

import java.util.ArrayList;

/**
 * Created by sean on 3/8/15.
 */
public final class IRExpressionTree extends IRTree {
    private IRExpression expr;

    public IRExpressionTree(IRExpression expr) {
        this.expr = expr;
    }

    public IRExpression getExpr() {
        return expr;
    }

    @Override
    public IRExpression unwrapExpression(TempFactory factory) {
        return expr;
    }

    @Override
    public IRStatement unwrapStatement(TempFactory factory) {
        return new IREvalAndDiscard(expr);
    }

    @Override
    public IRConditionalTree unwrapConditionalTree(TempFactory factory) {
        UpdateableTempLabel trueLabel = new UpdateableTempLabel();
        UpdateableTempLabel falseLabel = new UpdateableTempLabel();
        if (expr instanceof IRConst) {
            int constant = ((IRConst)expr).getImmediateValue();
            if (constant == 0) {
                // CONST 0 can be turned into an unconditional branch to a
                // false label.
                IRJump statement = new IRJump(falseLabel);
                return new IRConditionalTree(trueLabel, falseLabel, statement);
            } else {
                // CONST x can be turned into an unconditional branch to a
                // true label.
                IRJump statement = new IRJump(trueLabel);
                return new IRConditionalTree(trueLabel, falseLabel, statement);
            }
        }
        // otherwise, we need to turn it into a comparison with 0 with jumps.
        IRConditionalJump stmt = new IRConditionalJump(expr, new IRConst(0),
                IRRelationalOp.Neq, trueLabel, falseLabel);
        return new IRConditionalTree(trueLabel, falseLabel, stmt);
    }

    @Override
    public String toString() {
        return "IRExpressionTree{" +
                "expr=" + expr +
                '}';
    }
}
