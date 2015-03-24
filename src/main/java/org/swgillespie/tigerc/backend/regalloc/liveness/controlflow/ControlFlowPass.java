package org.swgillespie.tigerc.backend.regalloc.liveness.controlflow;

import org.swgillespie.tigerc.backend.instructionselection.Instruction;
import org.swgillespie.tigerc.backend.regalloc.graph.Graph;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

import java.util.List;

/**
 * Created by sean on 3/23/15.
 */
public class ControlFlowPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "control flow analysis";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        transFragments.getFragments()
                .stream()
                .filter(t -> t instanceof ProcFragment)
                .forEach(t -> {
                    ProcFragment proc = (ProcFragment)t;
                    List<Instruction> instructions = session.getInstructionCache().get(proc);
                    Graph<ControlFlowNode> cfg = ControlFlowGraphBuilder.buildControlFlowGraph(instructions);
                    session.getControlFlowCache().put(proc, cfg);
                });
        return transFragments;
    }
}
