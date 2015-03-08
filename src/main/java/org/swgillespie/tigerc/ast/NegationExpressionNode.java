package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class NegationExpressionNode extends ExpressionNode {
    private ExpressionNode negatedExpression;

    public NegationExpressionNode(TextSpan span, ExpressionNode negatedExpression) {
        super(span);
        this.negatedExpression = negatedExpression;
    }

    public ExpressionNode getNegatedExpression() {
        return negatedExpression;
    }

    public void setNegatedExpression(ExpressionNode negatedExpression) {
        this.negatedExpression = negatedExpression;
    }

    @Override
    public boolean isConst() {
        return negatedExpression.isConst();
    }

    @Override
    public String toString() {
        return "NegationExpressionNode{" +
                "negatedExpression=" + negatedExpression +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        negatedExpression.accept(visitor);
        visitor.exit(this);
    }
}
