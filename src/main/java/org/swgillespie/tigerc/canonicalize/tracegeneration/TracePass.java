package org.swgillespie.tigerc.canonicalize.tracegeneration;

import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

import java.util.List;

/**
 * Created by sean on 3/17/15.
 */
public class TracePass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "trace generation";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        TraceBuilder builder = new TraceBuilder(session);
        transFragments.getFragments().stream()
                .filter(t -> t instanceof ProcFragment)
                .forEach(f -> {
                    List<Trace> traces = builder.buildTraces((ProcFragment)f);
                    List<IRStatement> body = builder.flattenTrace(traces);
                    session.getProcBodyCache().put((ProcFragment)f, body);
                });
        return transFragments;
    }
}
