package org.swgillespie.tigerc.canonicalize.canontrans;

import org.swgillespie.tigerc.canonicalize.IRMutator;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRExpressionSequence;

/**
 * Created by sean on 3/16/15.
 */
public class ESeqRemovalVerifier extends IRMutator {
    @Override
    public IRExpression visitExpressionSequence(IRExpressionSequence node) {
        CompilerAssert.panic("failed to remove all ESEQ nodes from IR tree!");
        return null;
    }
}
