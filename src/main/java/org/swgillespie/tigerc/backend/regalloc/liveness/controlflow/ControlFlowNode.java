package org.swgillespie.tigerc.backend.regalloc.liveness.controlflow;

import org.swgillespie.tigerc.backend.instructionselection.Instruction;
import org.swgillespie.tigerc.trans.TempRegister;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sean on 3/23/15.
 */
public class ControlFlowNode {
    private Instruction instr;
    private Set<TempRegister> useSet;
    private Set<TempRegister> defSet;

    public ControlFlowNode(Instruction instr) {
        this.instr = instr;
        this.useSet = new HashSet<>();
        this.defSet = new HashSet<>();
    }

    public Instruction getInstr() {
        return instr;
    }

    public Set<TempRegister> getUseSet() {
        return useSet;
    }

    public Set<TempRegister> getDefSet() {
        return defSet;
    }

    public void setUseSet(Set<TempRegister> useSet) {
        this.useSet = useSet;
    }

    public void setDefSet(Set<TempRegister> defSet) {
        this.defSet = defSet;
    }
}
