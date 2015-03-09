package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public final class IRJump extends IRStatement {
    private IRExpression target;
    private List<? extends TempLabel> possibleTargetLabels;

    public IRJump(IRExpression target, List<? extends TempLabel> possibleTargetLabels) {
        this.target = target;
        this.possibleTargetLabels = possibleTargetLabels;
    }

    public IRExpression getTarget() {
        return target;
    }

    public List<? extends TempLabel> getPossibleTargetLabels() {
        return possibleTargetLabels;
    }

    @Override
    public String toString() {
        return "IRJump{" +
                "target=" + target +
                ", possibleTargetLabels=" + possibleTargetLabels +
                '}';
    }
}
