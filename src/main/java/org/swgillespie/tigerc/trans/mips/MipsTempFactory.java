package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/8/15.
 */
public class MipsTempFactory implements TempFactory {
    private int labelCounter;
    private int registerCounter;

    @Override
    public TempRegister newTemp() {
        return new MipsTempRegister(this.registerCounter++);
    }

    @Override
    public TempLabel newLabel() {
        return new MipsTempLabel("L" + labelCounter++);
    }

    @Override
    public TempLabel newNamedLabel(String name) {
        return new MipsTempLabel(name);
    }

    /**
     * $0 - always zero.
     */
    public TempRegister Zero = newTemp();

    /**
     * $at - reserved by the assembler.
     */
    public TempRegister AT = newTemp();

    /**
     * $v0 - integer function result
     */
    public TempRegister V0 = newTemp();

    /**
     * $v1 - static link
     */
    public TempRegister V1 = newTemp();

    /**
     * $a0..$a3 - first 4 integer-type function arguments
     */
    public TempRegister A0 = newTemp();
    public TempRegister A1 = newTemp();
    public TempRegister A2 = newTemp();
    public TempRegister A3 = newTemp();

    /**
     * $t0..$t9 - temporary registers, caller saved
     */
    public TempRegister T0 = newTemp();
    public TempRegister T1 = newTemp();
    public TempRegister T2 = newTemp();
    public TempRegister T3 = newTemp();
    public TempRegister T4 = newTemp();
    public TempRegister T5 = newTemp();
    public TempRegister T6 = newTemp();
    public TempRegister T7 = newTemp();
    public TempRegister T8 = newTemp();
    public TempRegister T9 = newTemp();

    /**
     * $s0..$s7 - temporary registers, callee saved
     */
    public TempRegister S0 = newTemp();
    public TempRegister S1 = newTemp();
    public TempRegister S2 = newTemp();
    public TempRegister S3 = newTemp();
    public TempRegister S4 = newTemp();
    public TempRegister S5 = newTemp();
    public TempRegister S6 = newTemp();
    public TempRegister S7 = newTemp();

    /**
     * $gp - global pointer
     */
    public TempRegister GP = newTemp();

    /**
     * $sp - stack pointer
     */
    public TempRegister SP = newTemp();

    /**
     * $fp - frame pointer
     */
    public TempRegister FP = newTemp();

    /**
     * $ra - return address
     */
    public TempRegister RA = newTemp();
}
