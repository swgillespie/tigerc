package org.swgillespie.tigerc.ast;

/**
 * Created by sean on 2/28/15.
 */
public abstract class ExpressionNode extends AstNode {
    public ExpressionNode(TextSpan span) {
        super(span);
    }

    public abstract boolean isConst();
}
