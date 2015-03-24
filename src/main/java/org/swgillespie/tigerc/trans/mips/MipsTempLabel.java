package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/8/15.
 */
public class MipsTempLabel extends TempLabel {
    public MipsTempLabel(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
