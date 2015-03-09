package org.swgillespie.tigerc.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/3/15.
 */
public final class ScopedSymbolTable<T> {
    private ScopedSymbolTable<T> parent;
    private Map<Symbol, T> scope;

    public ScopedSymbolTable(ScopedSymbolTable<T> parent) {
        this.parent = parent;
        this.scope = new HashMap<>();
    }

    public T query(Symbol key) {
        T value = this.scope.get(key);
        if (value == null && this.parent != null) {
            return this.parent.query(key);
        }
        return value;
    }

    public void add(Symbol key, T value) {
        this.scope.put(key, value);
    }

    public ScopedSymbolTable<T> getParent() {
        return parent;
    }

    public void setParent(ScopedSymbolTable<T>parent) {
        this.parent = parent;
    }
}
