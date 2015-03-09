package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.FrameAccess;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public abstract class MipsFrameAccess extends FrameAccess {
    public static MipsFrameAccess inFrame(int offset) {
        return new InFrame(offset);
    }

    public static MipsFrameAccess inRegister(TempRegister register) {
        return new InRegister(register);
    }
}
