package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.common.Symbol;

/**
 * Created by sean on 3/4/15.
 */
public class SymbolTable {
    private ScopedSymbolTable<Entry> entryTable;
    private ScopedSymbolTable<Type>  typeTable;

    public SymbolTable() {
        this.entryTable = new ScopedSymbolTable<>(null);
        this.typeTable = new ScopedSymbolTable<>(null);
    }

    public void enterScope() {
        this.entryTable = new ScopedSymbolTable<>(this.entryTable);
        this.typeTable = new ScopedSymbolTable<>(this.typeTable);
    }

    public void exitScope() {
        this.entryTable = this.entryTable.getParent();
        this.typeTable = this.typeTable.getParent();
    }

    public Entry queryEntry(Symbol name) {
        return this.entryTable.query(name);
    }

    public Type queryType(Symbol name) {
        return this.typeTable.query(name);
    }

    public void insertEntry(Symbol name, Entry value) {
        this.entryTable.add(name, value);
    }

    public void insertType(Symbol name, Type value) {
        this.typeTable.add(name, value);
    }
}
