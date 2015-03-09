package org.swgillespie.tigerc.trans;

/**
 * Created by sean on 3/8/15.
 */
public abstract class TempLabel {
    protected String name;

    protected TempLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
