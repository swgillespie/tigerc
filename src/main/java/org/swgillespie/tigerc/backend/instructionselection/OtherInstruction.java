package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.TempRegister;

import java.util.List;

/**
 * Created by sean on 3/18/15.
 */
public class OtherInstruction extends Instruction {
    private List<TempRegister> destinations;
    private List<TempRegister> sources;
    private AssemblyTargets targets;

    public OtherInstruction(String asm,
                            List<TempRegister> destinations,
                            List<TempRegister> sources,
                            AssemblyTargets targets) {
        super(asm);
        this.destinations = destinations;
        this.sources = sources;
        this.targets = targets;
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
        return targets;
    }
}
