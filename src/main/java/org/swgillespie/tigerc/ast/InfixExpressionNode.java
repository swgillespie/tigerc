package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class InfixExpressionNode extends ExpressionNode {
    private InfixOperator operator;
    private ExpressionNode left;
    private ExpressionNode right;

    public InfixExpressionNode(TextSpan span, InfixOperator operator, ExpressionNode left, ExpressionNode right) {
        super(span);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public InfixOperator getOperator() {
        return operator;
    }

    public void setOperator(InfixOperator operator) {
        this.operator = operator;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }

    @Override
    public boolean isConst() {
        // an infix operation is const if both the left and right sides are const.
        return left.isConst() && right.isConst();
    }

    @Override
    public String toString() {
        return "InfixExpressionNode{" +
                "operator=" + operator +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        left.accept(visitor);
        right.accept(visitor);
        visitor.exit(this);
    }
}
