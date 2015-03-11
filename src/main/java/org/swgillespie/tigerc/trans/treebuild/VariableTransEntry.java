package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.Access;

/**
 * Created by sean on 3/10/15.
 */
public final class VariableTransEntry extends TransEntry {
    private Access access;

    public VariableTransEntry(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "TransVarEntry{" +
                "access=" + access +
                '}';
    }
}
