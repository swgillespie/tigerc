package org.swgillespie.tigerc.backend.regalloc.liveness.controlflow;

import org.swgillespie.tigerc.backend.regalloc.graph.Graph;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/23/15.
 */
public class ControlFlowCache {
    private Map<ProcFragment, Graph<ControlFlowNode>> cfgs;

    public ControlFlowCache() {
        this.cfgs = new HashMap<>();
    }
    
    public void put(ProcFragment f, Graph<ControlFlowNode> cfg) {
        this.cfgs.put(f, cfg);
    }
    
    public Graph<ControlFlowNode> get(ProcFragment f) {
        return this.cfgs.get(f);
    }
}
