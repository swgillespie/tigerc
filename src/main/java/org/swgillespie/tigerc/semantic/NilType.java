package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/4/15.
 */
public final class NilType extends Type {
    public static final NilType Instance = new NilType();

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError() || (other instanceof NilType);
    }
}
