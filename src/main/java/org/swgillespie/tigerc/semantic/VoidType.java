package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/4/15.
 */
public final class VoidType extends Type {
    public static final VoidType Instance = new VoidType();

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError() || (other instanceof VoidType);
    }

    @Override
    public String toString() {
        return "VoidType";
    }
}
