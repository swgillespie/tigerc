package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class ArrayAccessNode extends LValueNode {
    private LValueNode base;
    private ExpressionNode index;

    public ArrayAccessNode(TextSpan span, LValueNode base, ExpressionNode index) {
        super(span);
        this.base = base;
        this.index = index;
    }

    public ExpressionNode getIndex() {
        return index;
    }

    public void setIndex(ExpressionNode index) {
        this.index = index;
    }

    public LValueNode getBase() {
        return base;
    }

    public void setBase(LValueNode base) {
        this.base = base;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "ArrayAccessNode{" +
                "base=" + base +
                ", index=" + index +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        base.accept(visitor);
        index.accept(visitor);
        visitor.exit(this);
    }
}
