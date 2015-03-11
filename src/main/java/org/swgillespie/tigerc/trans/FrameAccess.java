package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.trans.ir.IRExpression;

/**
 * Created by sean on 3/8/15.
 */
public abstract class FrameAccess {
    public abstract IRExpression toExpression(IRExpression framePointer, TempFactory factory);
}
