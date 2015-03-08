package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class ForExpressionNode extends ExpressionNode {
    private String identifier;
    private ExpressionNode bindingExpression;
    private ExpressionNode toExpression;
    private ExpressionNode body;

    public ForExpressionNode(TextSpan span, String identifier, ExpressionNode bindingExpression, ExpressionNode toExpression, ExpressionNode body) {
        super(span);
        this.identifier = identifier;
        this.bindingExpression = bindingExpression;
        this.toExpression = toExpression;
        this.body = body;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ExpressionNode getBindingExpression() {
        return bindingExpression;
    }

    public void setBindingExpression(ExpressionNode bindingExpression) {
        this.bindingExpression = bindingExpression;
    }

    public ExpressionNode getToExpression() {
        return toExpression;
    }

    public void setToExpression(ExpressionNode toExpression) {
        this.toExpression = toExpression;
    }

    public ExpressionNode getBody() {
        return body;
    }

    public void setBody(ExpressionNode body) {
        this.body = body;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "ForExpressionNode{" +
                "identifier='" + identifier + '\'' +
                ", bindingExpression=" + bindingExpression +
                ", toExpression=" + toExpression +
                ", body=" + body +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        bindingExpression.accept(visitor);
        toExpression.accept(visitor);
        visitor.enterBody(this);
        body.accept(visitor);
        visitor.exitBody(this);
        visitor.exit(this);
    }
}
