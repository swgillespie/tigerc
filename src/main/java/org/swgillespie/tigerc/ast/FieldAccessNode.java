package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class FieldAccessNode extends LValueNode {
    private LValueNode base;
    private String fieldName;

    public FieldAccessNode(TextSpan span, LValueNode base, String fieldName) {
        super(span);
        this.base = base;
        this.fieldName = fieldName;
    }

    public LValueNode getBase() {
        return base;
    }

    public void setBase(LValueNode base) {
        this.base = base;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "FieldAccessNode{" +
                "base=" + base +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        base.accept(visitor);
        visitor.exit(this);
    }
}
