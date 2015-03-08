package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public abstract class DeclarationNode extends AstNode {
    protected String name;

    public DeclarationNode(TextSpan span, String name) {
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
        return "DeclarationNode{" +
                "name='" + name + '\'' +
                '}';
    }
}
