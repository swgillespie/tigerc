package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/3/15.
 */
public final class IntegerType extends Type {
    public static final IntegerType Instance = new IntegerType();

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError() || (other instanceof IntegerType);
    }

    @Override
    public String toString() {
        return "int";
    }
}
