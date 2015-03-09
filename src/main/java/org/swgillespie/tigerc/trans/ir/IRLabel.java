package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/8/15.
 */
public final class IRLabel extends IRStatement {
    private TempLabel label;

    public IRLabel(TempLabel label) {
        this.label = label;
    }

    public TempLabel getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "IRLabel{" +
                "label=" + label +
                '}';
    }
}
