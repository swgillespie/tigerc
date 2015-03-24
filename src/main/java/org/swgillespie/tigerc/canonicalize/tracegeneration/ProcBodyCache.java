package org.swgillespie.tigerc.canonicalize.tracegeneration;

import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sean on 3/17/15.
 */
public class ProcBodyCache {
    private Map<ProcFragment, List<IRStatement>> bodies;

    public ProcBodyCache() {
        this.bodies = new HashMap<>();
    }

    public List<IRStatement> get(ProcFragment fragment) {
        return this.bodies.get(fragment);
    }

    public void put(ProcFragment fragment, List<IRStatement> body) {
        this.bodies.put(fragment, body);
    }
}
