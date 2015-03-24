package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.backend.instructionselection.mips.MipsInstructionSelectionPass;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.Target;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

/**
 * Created by sean on 3/19/15.
 */
public class InstructionSelectionPassFactory {
    public static CompilationPass<TransFragments, TransFragments> createPass(Target target) {
        switch (target) {
            case MIPS:
                return new MipsInstructionSelectionPass();
            default:
                CompilerAssert.panic("unknown target: " + target);
                return null;
        }
    }
}
