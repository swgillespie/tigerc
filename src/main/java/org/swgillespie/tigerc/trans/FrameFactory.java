package org.swgillespie.tigerc.trans;

/**
 * Created by sean on 3/5/15.
 */
public interface FrameFactory {
    StackFrame createFrame(TempLabel name, boolean[] formals);
}
