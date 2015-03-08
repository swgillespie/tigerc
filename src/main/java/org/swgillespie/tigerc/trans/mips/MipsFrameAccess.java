package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.FrameAccess;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/5/15.
 */
public abstract class MipsFrameAccess extends FrameAccess {
    public final class InRegister extends MipsFrameAccess {
        private TempRegister register;

        public InRegister(TempRegister register) {
            this.register = register;
        }
    }

    public final class InFrame extends MipsFrameAccess {
        private int offset;

        public InFrame(int offset) {
            this.offset = offset;
        }
    }
}
