package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/3/15.
 */
public final class StringType extends Type {
    public static final StringType Instance = new StringType();

    @Override
    public boolean isEquivalent(Type other) {
        return other.isError() || (other instanceof StringType);
    }

    @Override
    public String toString() {
        return "string";
    }
}
