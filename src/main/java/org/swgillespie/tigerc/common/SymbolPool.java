package org.swgillespie.tigerc.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/3/15.
 */
public class SymbolPool {
    private Map<String, Symbol> internedSymbols;

    public SymbolPool() {
        this.internedSymbols = new HashMap<>();
    }

    public Symbol getSymbol(String input) {
        Symbol interned = this.internedSymbols.get(input);
        if (interned != null) {
            return interned;
        }
        Symbol sym = new Symbol(input);
        this.internedSymbols.put(input, sym);
        return sym;
    }
}
