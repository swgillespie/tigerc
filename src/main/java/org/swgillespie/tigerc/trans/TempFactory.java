package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;

/**
 * Created by sean on 3/5/15.
 */
public interface TempFactory {
    TempLabel createLabel();
    TempLabel createNamedLabel();
    TempRegister createTemp();
}
