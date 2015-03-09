package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IREvalAndDiscard extends IRStatement {
    private IRExpression expr;

    public IREvalAndDiscard(IRExpression expr) {
        this.expr = expr;
    }

    public IRExpression getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "IREvalAndDiscard{" +
                "expr=" + expr +
                '}';
    }
}
