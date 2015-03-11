package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.ast.InfixOperator;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.ir.*;

/**
 * Created by sean on 3/8/15.
 */
final class InFrame extends MipsFrameAccess {
    private int offset;

    public InFrame(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public IRExpression toExpression(IRExpression framePointer, TempFactory factory) {
        /*
        InFrame represents a variable that lives on the stack at some offset from
        the frame pointer. "Loading" this value involves taking the frame pointer,
        adding the offset to it, and accessing the memory at that location.
        */
        return new IRMem(new IRBinop(InfixOperator.Plus,
                framePointer,
                new IRConst(offset)), MipsConstants.wordSize);
    }
}
