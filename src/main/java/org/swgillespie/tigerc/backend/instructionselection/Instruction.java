package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.TempRegister;

import java.util.List;

/**
 * Created by sean on 3/18/15.
 */
public abstract class Instruction {
    protected String asm;

    protected Instruction(String asm) {
        this.asm = asm;
    }

    public String getAsm() {
        return asm;
    }

    public abstract List<TempRegister> destinationRegs();
    public abstract List<TempRegister> sourceRegs();
    public abstract AssemblyTargets jumpTargets();

    @Override
    public String toString() {
        return asm + " (dest: " + this.destinationRegs() + ", src: " + this.sourceRegs() + ")";
    }
}
