package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempFactory;

/**
 * Created by sean on 3/8/15.
 */
public final class IRNoResultTree extends IRTree {
    private IRStatement stmt;

    public IRNoResultTree(IRStatement stmt) {
        this.stmt = stmt;
    }

    @Override
    public IRExpression unwrapExpression(TempFactory factory) {
        return new IRExpressionSequence(stmt, new IRConst(0));
    }

    @Override
    public IRStatement unwrapStatement(TempFactory factory) {
        return stmt;
    }

    @Override
    public IRConditionalTree unwrapConditionalTree(TempFactory factory) {
        return new IRConditionalTree(null, null, stmt);
    }

    public IRStatement getStmt() {
        return stmt;
    }

    @Override
    public String toString() {
        return "IRNoResultTree{" +
                "stmt=" + stmt +
                '}';
    }
}
