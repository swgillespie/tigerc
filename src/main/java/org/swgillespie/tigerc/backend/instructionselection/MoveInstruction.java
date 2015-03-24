package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.TempRegister;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sean on 3/18/15.
 */
public class MoveInstruction extends Instruction {
    private List<TempRegister> destinations;
    private List<TempRegister> sources;

    public MoveInstruction(String asm, List<TempRegister> destinations, List<TempRegister> sources) {
        super(asm);
        this.destinations = destinations;
        this.sources = sources;
    }

    @Override
    public List<TempRegister> destinationRegs() {
        return destinations;
    }

    @Override
    public List<TempRegister> sourceRegs() {
        return sources;
    }

    @Override
    public AssemblyTargets jumpTargets() {
        return new AssemblyTargets(Arrays.asList());
    }
}
