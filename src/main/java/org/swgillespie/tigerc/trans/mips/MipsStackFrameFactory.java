package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.StackFrame;
import org.swgillespie.tigerc.trans.StackFrameFactory;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;

import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public class MipsStackFrameFactory implements StackFrameFactory {
    private TempFactory tempFactory;

    public MipsStackFrameFactory(TempFactory tempFactory) {
        this.tempFactory = tempFactory;
    }

    @Override
    public StackFrame newFrame(TempLabel name, List<Boolean> formals) {
        return new MipsStackFrame(name, formals, this.tempFactory);
    }

    @Override
    public int getWordSize() {
        return 4;
    }
}
