package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class WhileExpressionNode extends ExpressionNode {
    private ExpressionNode condition;
    private ExpressionNode body;

    public WhileExpressionNode(TextSpan span, ExpressionNode condition, ExpressionNode body) {
        super(span);
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
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
        return "WhileExpressionNode{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        condition.accept(visitor);
        visitor.enterBody(this);
        body.accept(visitor);
        visitor.exitBody(this);
        visitor.exit(this);
    }
}
