package org.swgillespie.tigerc.trans.ir;

/**
 * Created by sean on 3/8/15.
 */
public final class IRMoveMem extends IRStatement {
    private IRExpression addressExpression;
    private int writeSize;
    private IRExpression value;

    public IRMoveMem(IRExpression addressExpression, int writeSize, IRExpression value) {
        this.addressExpression = addressExpression;
        this.writeSize = writeSize;
        this.value = value;
    }

    public IRExpression getAddressExpression() {
        return addressExpression;
    }

    public int getWriteSize() {
        return writeSize;
    }

    public IRExpression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IRMoveMem{" +
                "addressExpression=" + addressExpression +
                ", writeSize=" + writeSize +
                ", value=" + value +
                '}';
    }
}
