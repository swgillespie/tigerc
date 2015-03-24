package org.swgillespie.tigerc.optimization.constantfold;

import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlock;
import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlocks;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.treebuild.Fragment;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

import java.util.List;

/**
 * Created by sean on 3/16/15.
 */
public class ConstantFoldingPass implements CompilationPass<TransFragments, TransFragments> {
    @Override
    public String getName() {
        return "constant folding";
    }

    @Override
    public TransFragments runPass(CompilationSession session, TransFragments transFragments) {
        ConstantFolder folder = new ConstantFolder(session);
        for (Fragment f : transFragments.getFragments()) {
            if (f instanceof ProcFragment) {
                BasicBlocks blocks = session.getBasicBlockCache().get((ProcFragment)f);
                for (BasicBlock block : blocks.getBlocks()) {
                    List<IRStatement> statements = block.getStatements();
                    for (int i = 0; i < statements.size(); i++) {
                        statements.set(i, folder.visitStatement(statements.get(i)));
                    }
                }
            }
        }
        return transFragments;
    }
}
