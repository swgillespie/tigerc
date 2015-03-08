package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public final class TypeDeclarationNode extends DeclarationNode {
    private TypeNode type;

    public TypeDeclarationNode(TextSpan span, String name, TypeNode type) {
        super(span, name);
        this.type = type;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TypeDeclarationNode{" +
                "type=" + type +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        type.accept(visitor);
        visitor.exit(this);
    }
}
