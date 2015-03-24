package org.swgillespie.tigerc.canonicalize.canontrans;

import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

/**
 * Created by sean on 3/13/15.
 */
public class CanonicalMutationPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "ir tree canonicalization";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        CanonicalMutator mutator = new CanonicalMutator(session);
        ESeqRemovalVerifier verifier = new ESeqRemovalVerifier();
        transFragments.getFragments()
                .stream()
                .filter(frag -> frag instanceof ProcFragment)
                .forEach(frag -> {
                    ProcFragment proc = (ProcFragment) frag;
                    IRStatement mutated = mutator.visitStatement(proc.getBody());
                    mutated = mutator.linearize(mutated);
                    proc.setBody(mutated);
                    // TODO, when we're sure that we've fixed all canonical trans-related
                    // bugs, we can remove this sweeper. It just makes sure that there
                    // are no more ESEQs left in the IR tree.
                    verifier.visitStatement(mutated);
                });
        return transFragments;
    }
}
