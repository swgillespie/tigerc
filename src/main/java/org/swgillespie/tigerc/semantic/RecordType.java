package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.common.Symbol;

import java.util.List;
import java.util.Optional;

/**
 * Created by sean on 3/3/15.
 */
public final class RecordType extends Type {
    private List<RecordField> fields;

    public RecordType(List<RecordField> fields) {
        this.fields = fields;
    }

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError()
                || (other instanceof NilType)
                || this.equals(other);
    }

    @Override
    public boolean isRecord() {
        return true;
    }

    public boolean hasField(Symbol sym) {
        return fields.stream().anyMatch(i -> i.name == sym);
    }

    public List<RecordField> getFields() {
        return fields;
    }

    public void setFields(List<RecordField> fields) {
        this.fields = fields;
    }

    public Type getFieldType(Symbol sym) {
        Optional<Type> first = fields
                .stream()
                .filter(i -> i.name == sym)
                .findFirst()
                .map(i -> i.type);
        CompilerAssert.check(first.isPresent(), "attempted to get type of nonexistent field");
        return first.get();
    }

    @Override
    public String toString() {
        return "RecordType{" +
                "fields=" + fields +
                '}';
    }
}
