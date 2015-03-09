package org.swgillespie.tigerc.trans;

/**
 * Created by sean on 3/8/15.
 */
public interface TempFactory {
    TempRegister newTemp();
    TempLabel newLabel();
    TempLabel newNamedLabel(String name);
}
