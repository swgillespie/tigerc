package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.Level;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.treebuild.TransEntry;

/**
 * Created by sean on 3/10/15.
 */
public final class FunctionTransEntry extends TransEntry {
    private Level level;
    private TempLabel label;

    public FunctionTransEntry(Level level, TempLabel label) {
        this.level = level;
        this.label = label;
    }

    public Level getLevel() {
        return level;
    }

    public TempLabel getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "TransFunctionEntry{" +
                "level=" + level +
                ", label=" + label +
                '}';
    }
}
