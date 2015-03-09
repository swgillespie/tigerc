package org.swgillespie.tigerc.trans.escape;

/**
 * Created by sean on 3/8/15.
 */
public final class VariableEscapeEntry extends EscapeEntry {
    private boolean hasEscaped;

    public VariableEscapeEntry(int depth) {
        super(depth);
    }

    public boolean hasEscaped() {
        return hasEscaped;
    }

    public void setHasEscaped(boolean hasEscaped) {
        this.hasEscaped = hasEscaped;
    }
}
