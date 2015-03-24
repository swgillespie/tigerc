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
        // for debugging purposes only!
        MipsTempFactory temp = new MipsTempFactory();
        String canonicalName = temp.getCanonicalRegisterName(this);
        if (canonicalName == null) {
            return "%" + number;
        }
        return canonicalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MipsTempRegister that = (MipsTempRegister) o;

        if (number != that.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
