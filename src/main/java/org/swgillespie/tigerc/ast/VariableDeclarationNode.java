package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 3/2/15.
 */
public final class VariableDeclarationNode extends DeclarationNode {
    private ExpressionNode initializer;
    private IdentifierTypeNode type;

    public VariableDeclarationNode(TextSpan span, String name, ExpressionNode initializer, IdentifierTypeNode type) {
        super(span, name);
        this.initializer = initializer;
        this.type = type;
    }

    public boolean hasType() {
        return type != null;
    }

    public ExpressionNode getInitializer() {
        return initializer;
    }

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
    }

    public IdentifierTypeNode getType() {
        return type;
    }

    public void setType(IdentifierTypeNode type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" +
                "initializer=" + initializer +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        initializer.accept(visitor);
        if (this.hasType()) {
            type.accept(visitor);
        }
        visitor.exit(this);
    }
}
