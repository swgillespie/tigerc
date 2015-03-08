package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public final class FieldDeclarationNode extends AstNode {
    private String name;
    private IdentifierTypeNode type;

    public FieldDeclarationNode(TextSpan span, String name, IdentifierTypeNode type) {
        super(span);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdentifierTypeNode getType() {
        return type;
    }

    public void setType(IdentifierTypeNode type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FieldDeclarationNode{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        type.accept(visitor);
        visitor.exit(this);
    }
}
