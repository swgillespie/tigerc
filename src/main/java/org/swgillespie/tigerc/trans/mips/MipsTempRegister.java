package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public class MipsTempRegister extends TempRegister {
    private int number;

    public MipsTempRegister(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "MipsTempRegister{" +
                "number=" + number +
                '}';
    }
}
