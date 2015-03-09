package org.swgillespie.tigerc.trans;

import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public interface StackFrameFactory {
    StackFrame newFrame(TempLabel name, List<Boolean> formals);
    int getWordSize();
}
