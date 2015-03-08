package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/3/15.
 */
public final class ArrayType extends Type {
    private Type baseType;

    public ArrayType(Type baseType) {
        this.baseType = baseType;
    }

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError() || this.equals(other);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    public Type getBaseType() {
        return baseType;
    }

    public void setBaseType(Type baseType) {
        this.baseType = baseType;
    }
}
