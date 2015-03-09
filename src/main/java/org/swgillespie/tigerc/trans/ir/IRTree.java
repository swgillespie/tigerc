package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

import java.util.ArrayList;

/**
 * Created by sean on 3/8/15.
 */
public abstract class IRTree {
    public abstract IRExpression unwrapExpression(TempFactory factory);
    public abstract IRStatement unwrapStatement(TempFactory factory);
    public abstract IRConditionalTree unwrapConditionalTree(TempFactory factory);
}
