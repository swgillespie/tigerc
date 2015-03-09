package org.swgillespie.tigerc.trans.escape;

/**
 * Created by sean on 3/8/15.
 */
public abstract class EscapeEntry {
    protected int depth;

    protected EscapeEntry(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }
}
