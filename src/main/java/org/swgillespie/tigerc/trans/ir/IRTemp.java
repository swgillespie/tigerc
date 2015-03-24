package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public final class IRTemp extends IRExpression {
    private TempRegister temp;

    public IRTemp(TempRegister temp) {
        this.temp = temp;
    }

    public TempRegister getTemp() {
        return temp;
    }

    @Override
    public String toString() {
        return temp.toString();
    }
}
