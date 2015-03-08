package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/27/15.
 */
public final class RecordTypeNode extends TypeNode {
    private List<FieldDeclarationNode> fields;

    public RecordTypeNode(TextSpan span, List<FieldDeclarationNode> fields) {
        super(span);
        this.fields = fields;
    }

    public List<FieldDeclarationNode> getFields() {
        return fields;
    }

    public void setFields(List<FieldDeclarationNode> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "RecordTypeNode{" +
                "fields=" + fields +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        fields.forEach(f -> f.accept(visitor));
        visitor.exit(this);
    }
}
