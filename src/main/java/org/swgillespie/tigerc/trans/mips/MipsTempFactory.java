package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public class MipsTempFactory implements TempFactory {
    private int labelCounter;
    private int registerCounter;

    @Override
    public TempRegister newTemp() {
        return new MipsTempRegister(this.registerCounter++);
    }

    @Override
    public TempLabel newLabel() {
        return new MipsTempLabel("L" + labelCounter++);
    }

    @Override
    public TempLabel newNamedLabel(String name) {
        return new MipsTempLabel(name);
    }
}
