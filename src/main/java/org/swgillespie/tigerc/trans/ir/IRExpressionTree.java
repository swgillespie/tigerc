package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempFactory;

import java.util.ArrayList;

/**
 * Created by sean on 3/8/15.
 */
public class IRExpressionTree extends IRTree {
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
        if (expr instanceof IRConst) {
            int constant = ((IRConst)expr).getImmediateValue();
            if (constant == 0) {
                // CONST 0 can be turned into an unconditional branch to a
                // false label.
                UpdateableTempLabel label = new UpdateableTempLabel();
                ArrayList<UpdateableTempLabel> branches = new ArrayList<>();
                branches.add(label);
                IRJump statement = new IRJump(new IRName(label), branches);
                return new IRConditionalTree(label, null, statement);
            } else {
                // CONST x can be turned into an unconditional branch to a
                // true label.
                UpdateableTempLabel label = new UpdateableTempLabel();
                ArrayList<UpdateableTempLabel> branches = new ArrayList<>();
                branches.add(label);
                IRJump statement = new IRJump(new IRName(label), branches);
                return new IRConditionalTree(null, label, statement);
            }
        }
        // otherwise, we need to turn it into a comparison with 0 with jumps.
        UpdateableTempLabel trueBranch = new UpdateableTempLabel();
        UpdateableTempLabel falseBranch = new UpdateableTempLabel();
        IRConditionalJump stmt = new IRConditionalJump(expr, new IRConst(1),
                IRRelationalOp.Eq, trueBranch, falseBranch);
        return new IRConditionalTree(trueBranch, falseBranch, stmt);
    }

    @Override
    public String toString() {
        return "IRExpressionTree{" +
                "expr=" + expr +
                '}';
    }
}
