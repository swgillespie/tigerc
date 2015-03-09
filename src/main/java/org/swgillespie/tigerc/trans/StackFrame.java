package org.swgillespie.tigerc.trans;

import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public abstract class StackFrame {
    protected TempLabel name;
    protected List<Boolean> formalEscapes;

    protected StackFrame(TempLabel name, List<Boolean> formalEscapes) {
        this.name = name;
        this.formalEscapes = formalEscapes;
    }

    public TempLabel getName() {
        return name;
    }

    public abstract List<? extends FrameAccess> getFormals();

    public abstract FrameAccess allocLocal(boolean escape);
}
