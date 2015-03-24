package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.ir.IRStatement;

/**
 * Created by sean on 3/18/15.
 */
public abstract class InstructionSelector {
    public abstract void munchStatement(IRStatement stmt);
}
