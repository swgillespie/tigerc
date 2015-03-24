package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public final class IRJump extends IRStatement {
    private IRExpression target;
    private List<TempLabel> possibleTargetLabels;

    public IRJump(IRExpression target, List<TempLabel> possibleTargetLabels) {
        this.target = target;
        this.possibleTargetLabels = possibleTargetLabels;
    }

    public IRJump(TempLabel label) {
        this.target = new IRName(label);
        this.possibleTargetLabels = new ArrayList<>();
        this.possibleTargetLabels.add(label);
    }

    public IRExpression getTarget() {
        return target;
    }

    public List<TempLabel> getPossibleTargetLabels() {
        return possibleTargetLabels;
    }

    public void setTarget(IRExpression target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "jump " + target;
    }
}
