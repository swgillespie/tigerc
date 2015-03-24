package org.swgillespie.tigerc.backend.regalloc.liveness.controlflow;

import org.swgillespie.tigerc.backend.instructionselection.Instruction;
import org.swgillespie.tigerc.backend.instructionselection.LabelInstruction;
import org.swgillespie.tigerc.backend.regalloc.graph.Graph;
import org.swgillespie.tigerc.backend.regalloc.graph.NaiveGraph;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempLabel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sean on 3/23/15.
 */
public class ControlFlowGraphBuilder {
    public static Graph<ControlFlowNode> buildControlFlowGraph(List<Instruction> instructions) {
        Graph<ControlFlowNode> cfg = new NaiveGraph<>();
        Map<TempLabel, ControlFlowNode> labels = new HashMap<>();
        // first pass - create nodes for all of the instructions and stick
        // them in the graph
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instr = instructions.get(i);
            if (instr instanceof LabelInstruction) {
                CompilerAssert.check(i < instructions.size() - 1, "a function should never end in a label");
                TempLabel label = ((LabelInstruction) instr).getLabel();
                // put the next element into this map.
                ControlFlowNode next = new ControlFlowNode(instructions.get(i + 1));
                cfg.addVertex(next);
                labels.put(label, next);
                i += 1;
            }
            ControlFlowNode node = new ControlFlowNode(instr);
            cfg.addVertex(node);
        }
        // second pass - add edges between the nodes based on the control flow.
        ControlFlowNode prev = null;
        for (Instruction instr : instructions) {
            ControlFlowNode thisInstr = new ControlFlowNode(instr);
            if (prev != null) {
                cfg.addEdge(prev, thisInstr);
            }
            if (instr.jumpTargets().getTargets().size() != 0) {
                // if this instruction jumps somewhere, add
                // edges to every location that this instruction could possibly
                // jump to
                for (TempLabel target : instr.jumpTargets().getTargets()) {
                    ControlFlowNode targetNode = labels.get(target);
                    CompilerAssert.check(targetNode != null, "CFG node not found in graph?");
                    cfg.addEdge(thisInstr, targetNode);
                }
            }
            // otherwise, it's just going to proceed forward.
            prev = thisInstr;
        }
        return cfg;
    }
}
