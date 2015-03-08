package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public final class ArrayTypeNode extends TypeNode {
    private IdentifierTypeNode arrayTypeName;

    public ArrayTypeNode(TextSpan span, IdentifierTypeNode arrayTypeName) {
        super(span);
        this.arrayTypeName = arrayTypeName;
    }

    public IdentifierTypeNode getArrayTypeName() {
        return arrayTypeName;
    }

    public void setArrayTypeName(IdentifierTypeNode arrayTypeName) {
        this.arrayTypeName = arrayTypeName;
    }

    @Override
    public String toString() {
        return "ArrayTypeNode{" +
                "arrayTypeName='" + arrayTypeName + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        arrayTypeName.accept(visitor);
        visitor.exit(this);
    }
}
