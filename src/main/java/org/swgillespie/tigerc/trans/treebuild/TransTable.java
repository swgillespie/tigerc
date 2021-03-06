package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.common.ScopedSymbolTable;
import org.swgillespie.tigerc.common.Symbol;

/**
 * Created by sean on 3/10/15.
 */
public class TransTable {
    private ScopedSymbolTable<TransEntry> entries;

    public TransTable() {
        this.entries = new ScopedSymbolTable<>(null);
    }

    public void enterScope() {
        this.entries = new ScopedSymbolTable<>(this.entries);
    }

    public void exitScope() {
        this.entries = this.entries.getParent();
    }

    public TransEntry query(Symbol sym) {
        TransEntry entry = this.entries.query(sym);
        CompilerAssert.check(entry != null, "entry for symbol " + sym + " was null!");
        return entry;
    }

    public void insert(Symbol sym, TransEntry entry) {
        this.entries.add(sym, entry);
    }
}
