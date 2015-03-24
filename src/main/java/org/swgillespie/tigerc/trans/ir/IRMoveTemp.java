package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRMoveTemp extends IRStatement {
    private IRTemp destination;
    private IRExpression value;

    public IRMoveTemp(IRTemp destination, IRExpression value) {
        this.destination = destination;
        this.value = value;
    }

    public IRTemp getDestination() {
        return destination;
    }

    public IRExpression getValue() {
        return value;
    }

    public void setValue(IRExpression value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return destination + " <- " + value;
    }
}
