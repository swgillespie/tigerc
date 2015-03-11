package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRStatement;

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

    public abstract TempRegister framePointer();

    public abstract TempRegister returnValue();

    public abstract IRStatement procEntryExit(IRStatement statement);
}
