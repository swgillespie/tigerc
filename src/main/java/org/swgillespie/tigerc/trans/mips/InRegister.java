package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
final class InRegister extends MipsFrameAccess {
    private TempRegister register;

    public InRegister(TempRegister register) {
        this.register = register;
    }

    public TempRegister getRegister() {
        return register;
    }
}
