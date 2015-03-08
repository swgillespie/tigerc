package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class AssignmentNode extends ExpressionNode {
    private LValueNode assignmentTarget;
    private ExpressionNode expression;

    public AssignmentNode(TextSpan span, LValueNode assignmentTarget, ExpressionNode expression) {
        super(span);
        this.assignmentTarget = assignmentTarget;
        this.expression = expression;
    }

    public LValueNode getAssignmentTarget() {
        return assignmentTarget;
    }

    public void setAssignmentTarget(LValueNode assignmentTarget) {
        this.assignmentTarget = assignmentTarget;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "AssignmentNode{" +
                "assignmentTarget=" + assignmentTarget +
                ", expression=" + expression +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        assignmentTarget.accept(visitor);
        expression.accept(visitor);
        visitor.exit(this);
    }
}
