package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempRegister;
import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRMem;
import org.swgillespie.tigerc.trans.ir.IRTemp;

/**
 * Created by sean on 3/8/15.
 */
final class InRegister extends MipsFrameAccess {
    private TempRegister register;

    public InRegister(TempRegister register) {
        this.register = register;
    }

    public TempRegister getRegister() {
        return register;
    }

    @Override
    public IRExpression toExpression(IRExpression framePointer, TempFactory factory) {
        /*
        InRegister represents a variable that lives in a register. "Loading" that variable
        is as simple as accessing the register in which this value lives. Since we aren't
        accessing this value from the stack, we can ignore the framePointer parameter.
         */
        return new IRMem(new IRTemp(register), MipsConstants.wordSize);
    }
}
