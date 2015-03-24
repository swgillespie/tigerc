package org.swgillespie.tigerc.backend.instructionselection.mips;

import org.swgillespie.tigerc.backend.instructionselection.Instruction;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

import java.util.List;

/**
 * Created by sean on 3/19/15.
 */
public class MipsInstructionSelectionPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "mips instruction selection";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        MipsInstructionSelector selector = new MipsInstructionSelector(session);
        transFragments.getFragments()
                .stream()
                .filter(i -> i instanceof ProcFragment)
                .forEach(i -> {
                    ProcFragment proc = (ProcFragment)i;
                    List<IRStatement> flatBody = session.getProcBodyCache().get(proc);
                    flatBody.forEach(selector::munchStatement);
                    List<Instruction> instructions = selector.getInstructions();
                    session.getInstructionCache().put(proc, instructions);
                    selector.clearInstructions();
                });
        return transFragments;
    }
}
