package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sean on 3/19/15.
 */
public class InstructionCache {
    private Map<ProcFragment, List<Instruction>> instructions;

    public InstructionCache() {
        this.instructions = new HashMap<>();
    }

    public void put(ProcFragment fragment, List<Instruction> instructions) {
        this.instructions.put(fragment, instructions);
    }

    public List<Instruction> get(ProcFragment fragment) {
        return this.instructions.get(fragment);
    }
}
