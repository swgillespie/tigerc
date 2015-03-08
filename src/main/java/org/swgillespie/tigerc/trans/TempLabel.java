package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.common.Symbol;

/**
 * Created by sean on 3/5/15.
 */
public abstract class TempLabel {
    protected Symbol name;

    protected TempLabel(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return this.name;
    }
}
