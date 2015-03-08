package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/27/15.
 */
public abstract class AstNode {
    protected TextSpan span;

    public AstNode(TextSpan span) {
        this.span = span;
    }

    @Override
    public String toString() {
        return "ExpressionNode{" +
                "span=" + span +
                '}';
    }

    public TextSpan getSpan() {
        return span;
    }

    public void setSpan(TextSpan span) {
        this.span = span;
    }

    public abstract void accept(AstVisitor visitor);
}
