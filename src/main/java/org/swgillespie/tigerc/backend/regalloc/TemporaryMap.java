package org.swgillespie.tigerc.backend.regalloc;

import org.swgillespie.tigerc.trans.TempRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/18/15.
 */
public class TemporaryMap {
    private Map<TempRegister, String> bindings;
    private TemporaryMap childMap;

    public TemporaryMap() {
        this(null);
    }

    public TemporaryMap(TemporaryMap layeredMap) {
        this.bindings = new HashMap<>();
        this.childMap = layeredMap;
    }

    public void put(TempRegister register, String binding) {
        this.bindings.put(register, binding);
    }

    public String get(TempRegister register) {
        if (this.childMap != null) {
            String binding = this.childMap.get(register);
            if (binding != null) {
                return binding;
            }
        }
        return this.bindings.get(register);
    }
}
