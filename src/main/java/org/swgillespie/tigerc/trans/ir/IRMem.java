package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRMem extends IRExpression {
    private IRExpression address;
    private int wordSize; // in bytes

    public IRMem(IRExpression address, int wordSize) {
        this.address = address;
        this.wordSize = wordSize;
    }

    public IRExpression getAddress() {
        return address;
    }

    public int getWordSize() {
        return wordSize;
    }

    @Override
    public String toString() {
        return "IRMem{" +
                "address=" + address +
                ", wordSize=" + wordSize +
                '}';
    }
}
