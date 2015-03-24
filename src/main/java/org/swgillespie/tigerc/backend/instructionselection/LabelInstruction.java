package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sean on 3/18/15.
 */
public class LabelInstruction extends Instruction {
    private TempLabel label;

    public LabelInstruction(String asm, TempLabel label) {
        super(asm);
        this.label = label;
    }

    public TempLabel getLabel() {
        return label;
    }

    @Override
    public List<TempRegister> destinationRegs() {
        return Arrays.asList();
    }

    @Override
    public List<TempRegister> sourceRegs() {
        return Arrays.asList();
    }

    @Override
    public AssemblyTargets jumpTargets() {
        return new AssemblyTargets(Arrays.asList(label));
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
