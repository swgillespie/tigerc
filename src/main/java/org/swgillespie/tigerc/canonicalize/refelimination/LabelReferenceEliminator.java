package org.swgillespie.tigerc.canonicalize.refelimination;

import org.swgillespie.tigerc.canonicalize.IRMutator;
import org.swgillespie.tigerc.trans.ir.IRConditionalJump;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import org.swgillespie.tigerc.trans.ir.UpdateableTempLabel;

/**
 * Created by sean on 3/16/15.
 */
public class LabelReferenceEliminator extends IRMutator {
    @Override
    public IRStatement visitConditionalJump(IRConditionalJump node) {
        if (node.getTrueTarget() instanceof UpdateableTempLabel) {
            node.setTrueTarget(((UpdateableTempLabel) node.getTrueTarget()).getLabel());
        }
        if (node.getFalseTarget() instanceof UpdateableTempLabel) {
            node.setFalseTarget(((UpdateableTempLabel) node.getFalseTarget()).getLabel());
        }
        return node;
    }
}
