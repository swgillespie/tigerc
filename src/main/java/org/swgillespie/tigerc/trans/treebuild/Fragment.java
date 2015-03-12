package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.StackFrame;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.ir.IRStatement;

/**
 * Created by sean on 3/11/15.
 */
public abstract class Fragment {
    public static Fragment stringFragment(TempLabel name, String str) {
        return new StringFragment(name, str);
    }

    public static Fragment procFragment(IRStatement body, StackFrame frame) {
        return new ProcFragment(body, frame);
    }
}
