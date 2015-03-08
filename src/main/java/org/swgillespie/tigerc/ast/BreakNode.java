package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 3/2/15.
 */
public final class BreakNode extends ExpressionNode {
    public BreakNode(TextSpan span) {
        super(span);
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "BreakNode{}";
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }
}
