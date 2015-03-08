package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 3/2/15.
 */
public final class IdentifierNode extends LValueNode {
    private String identifier;

    public IdentifierNode(TextSpan span, String identifier) {
        super(span);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "IdentifierNode{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }
}
