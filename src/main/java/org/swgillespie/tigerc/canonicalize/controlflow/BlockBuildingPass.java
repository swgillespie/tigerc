package org.swgillespie.tigerc.canonicalize.controlflow;

import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

/**
 * Created by sean on 3/16/15.
 */
public class BlockBuildingPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "block building";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        BasicBlockBuilder builder = new BasicBlockBuilder(session);
        transFragments.getFragments()
                .stream()
                .filter(f -> f instanceof ProcFragment)
                .forEach(f -> builder.buildBlocks((ProcFragment) f));
        return transFragments;
    }
}
