package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/28/15.
 */
public final class RecordCreationExpressionNode extends ExpressionNode {
    private IdentifierTypeNode typeName;
    private List<FieldCreationNode> fields;

    public RecordCreationExpressionNode(TextSpan span, IdentifierTypeNode typeName, List<FieldCreationNode> fields) {
        super(span);
        this.typeName = typeName;
        this.fields = fields;
    }

    public IdentifierTypeNode getTypeName() {
        return typeName;
    }

    public void setTypeName(IdentifierTypeNode typeName) {
        this.typeName = typeName;
    }

    public List<FieldCreationNode> getFields() {
        return fields;
    }

    public void setFields(List<FieldCreationNode> fields) {
        this.fields = fields;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "RecordCreationExpressionNode{" +
                "typeName='" + typeName + '\'' +
                ", fields=" + fields +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        typeName.accept(visitor);
        fields.forEach(f -> f.accept(visitor));
        visitor.exit(this);
    }
}
