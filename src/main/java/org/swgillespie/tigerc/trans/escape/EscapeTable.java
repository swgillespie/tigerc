package org.swgillespie.tigerc.trans.escape;

import org.swgillespie.tigerc.common.ScopedSymbolTable;
import org.swgillespie.tigerc.common.Symbol;

/**
 * Created by sean on 3/8/15.
 */
final class EscapeTable {
    private ScopedSymbolTable<EscapeEntry> table;

    public EscapeTable() {
        this.table = new ScopedSymbolTable<>(null);
    }

    public void enterScope() {
        this.table = new ScopedSymbolTable<>(this.table);
    }

    public void exitScope() {
        this.table = this.table.getParent();
    }

    public EscapeEntry query(Symbol name) {
        return this.table.query(name);
    }

    public void insert(Symbol name, EscapeEntry entry) {
        this.table.add(name, entry);
    }
}
