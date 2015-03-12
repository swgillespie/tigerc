package org.swgillespie.tigerc.trans;

/**
 * Created by sean on 3/8/15.
 */
public interface TempFactory {
    TempRegister newTemp();
    TempLabel newLabel();
    TempLabel newNamedLabel(String name);

    // runtime functions!
    TempLabel print();
    TempLabel flush();
    TempLabel getchar();
    TempLabel ord();
    TempLabel chr();
    TempLabel size();
    TempLabel substring();
    TempLabel concat();
    TempLabel not();
    TempLabel exit();
    TempLabel strcmp();
    TempLabel malloc();
    TempLabel memset();
}
