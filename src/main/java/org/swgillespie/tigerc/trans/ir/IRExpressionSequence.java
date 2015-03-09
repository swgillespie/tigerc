package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRExpressionSequence extends IRExpression {
    private IRStatement stmt;
    private IRExpression result;

    public IRExpressionSequence(IRStatement stmt, IRExpression result) {
        this.stmt = stmt;
        this.result = result;
    }

    public IRStatement getStmt() {
        return stmt;
    }

    public IRExpression getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "IRExpressionSequence{" +
                "stmt=" + stmt +
                ", result=" + result +
                '}';
    }
}
