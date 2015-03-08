package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public final class IdentifierTypeNode extends TypeNode {
    private String name;

    public IdentifierTypeNode(TextSpan span, String name) {
        super(span);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierTypeNode{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }
}
