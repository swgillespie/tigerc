package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class ConditionalExpressionNode extends ExpressionNode {
    private ExpressionNode condition;
    private ExpressionNode trueBranch;
    private ExpressionNode falseBranch;

    public ConditionalExpressionNode(TextSpan span, ExpressionNode condition, ExpressionNode trueBranch, ExpressionNode falseBranch) {
        super(span);
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public boolean hasFalseBranch() {
        return falseBranch != null;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public ExpressionNode getTrueBranch() {
        return trueBranch;
    }

    public void setTrueBranch(ExpressionNode trueBranch) {
        this.trueBranch = trueBranch;
    }

    public ExpressionNode getFalseBranch() {
        return falseBranch;
    }

    public void setFalseBranch(ExpressionNode falseBranch) {
        this.falseBranch = falseBranch;
    }

    @Override
    public boolean isConst() {
        // a conditional expression is const if its condition is known to be const and
        // the branch chosen by the const condition is const. This isn't known at the ast stage,
        // so we choose a more restrictive rule that both branches of the conditional expression
        // must be const.
        return condition.isConst() && trueBranch.isConst() && (!hasFalseBranch() || falseBranch.isConst());
    }

    @Override
    public String toString() {
        return "ConditionalExpression{" +
                "condition=" + condition +
                ", trueBranch=" + trueBranch +
                ", falseBranch=" + falseBranch +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        condition.accept(visitor);
        trueBranch.accept(visitor);
        if (this.hasFalseBranch()) {
            falseBranch.accept(visitor);
        }
        visitor.exit(this);
    }
}
