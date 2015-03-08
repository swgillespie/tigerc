package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class NilNode extends ExpressionNode {
    public NilNode(TextSpan span) {
        super(span);
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }
}
