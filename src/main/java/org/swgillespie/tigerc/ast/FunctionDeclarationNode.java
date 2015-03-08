package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/27/15.
 */
public final class FunctionDeclarationNode extends DeclarationNode {
    private List<FieldDeclarationNode> parameters;
    private IdentifierTypeNode returnType;
    private ExpressionNode body;

    public FunctionDeclarationNode(TextSpan span,
                                   String name,
                                   List<FieldDeclarationNode> parameters,
                                   IdentifierTypeNode returnType,
                                   ExpressionNode body) {
        super(span, name);
        this.returnType = returnType;
        this.parameters = parameters;
        this.body = body;
    }

    public boolean hasReturnType() {
        return this.returnType != null;
    }

    public List<FieldDeclarationNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<FieldDeclarationNode> parameters) {
        this.parameters = parameters;
    }

    public IdentifierTypeNode getReturnType() {
        return returnType;
    }

    public void setReturnType(IdentifierTypeNode returnType) {
        this.returnType = returnType;
    }

    public ExpressionNode getBody() {
        return body;
    }

    public void setBody(ExpressionNode body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "FunctionDeclarationNode{" +
                "parameters=" + parameters +
                ", returnType='" + returnType + '\'' +
                ", body=" + body +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        parameters.forEach(p -> p.accept(visitor));
        if (hasReturnType()) {
            returnType.accept(visitor);
        }
        visitor.enterBody(this);
        body.accept(visitor);
        visitor.exitBody(this);
        visitor.exit(this);
    }
}
