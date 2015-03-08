package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class FieldCreationNode extends AstNode {
    private String fieldName;
    private ExpressionNode fieldValue;

    public FieldCreationNode(TextSpan span, String fieldName, ExpressionNode fieldValue) {
        super(span);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ExpressionNode getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(ExpressionNode fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        return "FieldCreationNode{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldValue=" + fieldValue +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        fieldValue.accept(visitor);
        visitor.exit(this);
    }
}
