package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/28/15.
 */
public final class CallExpressionNode extends ExpressionNode {
    private String functionName;
    private List<ExpressionNode> parameters;

    public CallExpressionNode(TextSpan span, String functionName, List<ExpressionNode> parameters) {
        super(span);
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<ExpressionNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ExpressionNode> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "CallExpressionNode{" +
                "functionName='" + functionName + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        parameters.forEach(p -> p.accept(visitor));
        visitor.exit(this);
    }
}
