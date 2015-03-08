package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class IntegerLiteralNode extends ExpressionNode {
    private int value;

    public IntegerLiteralNode(TextSpan span, int value) {
        super(span);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return "IntegerLiteralNode{" +
                "value=" + value +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }
}
