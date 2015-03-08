package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.common.Symbol;

import java.util.List;

/**
 * Created by sean on 3/5/15.
 */
public interface StackFrame {
    public abstract Symbol getName();
    public abstract List<FrameAccess> getFormals();
    public abstract FrameAccess allocLocal(boolean escape);
}
