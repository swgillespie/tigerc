package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRConst extends IRExpression {
    private int immediateValue;

    public IRConst(int immediateValue) {
        this.immediateValue = immediateValue;
    }

    public int getImmediateValue() {
        return immediateValue;
    }

    @Override
    public String toString() {
        return "#" + immediateValue;
    }
}
