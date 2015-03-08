package org.swgillespie.tigerc.ast;

/**
 * Created by sean on 2/28/15.
 */
public abstract class LValueNode extends ExpressionNode {
    public LValueNode(TextSpan span) {
        super(span);
    }

    @Override
    public boolean isConst() {
        return false;
    }
}
