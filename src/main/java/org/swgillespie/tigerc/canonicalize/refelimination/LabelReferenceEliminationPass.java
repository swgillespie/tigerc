package org.swgillespie.tigerc.canonicalize.refelimination;

import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

/**
 * Created by sean on 3/16/15.
 */
public class LabelReferenceEliminationPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "label reference elimination";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        LabelReferenceEliminator mutator = new LabelReferenceEliminator();
        transFragments.getFragments()
                .stream()
                .filter(f -> f instanceof ProcFragment)
                .forEach(f -> {
                    ProcFragment proc = (ProcFragment) f;
                    proc.setBody(mutator.visitStatement(proc.getBody()));
                });
        return transFragments;
    }
}
