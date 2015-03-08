package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/3/15.
 */
public abstract class Type {
    public abstract boolean isEquivalent(Type other);

    public boolean isError() {
        return false;
    }
    public boolean isArray() { return false; }
    public boolean isRecord() { return false; }
}
