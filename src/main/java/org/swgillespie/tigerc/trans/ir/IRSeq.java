package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRSeq extends IRStatement {
    private IRStatement first;
    private IRStatement second;

    public IRSeq(IRStatement first, IRStatement second) {
        this.first = first;
        this.second = second;
    }

    public IRStatement getFirst() {
        return first;
    }

    public IRStatement getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "IRSeq{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
