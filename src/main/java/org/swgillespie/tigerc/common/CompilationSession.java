package org.swgillespie.tigerc.common;

import org.swgillespie.tigerc.semantic.TypeCache;
import org.swgillespie.tigerc.trans.escape.EscapeEntryCache;

/**
 * Created by sean on 3/2/15.
 */
public class CompilationSession {
    private DiagnosticSink diagnostics;
    private String currentFile;
    private SymbolPool symbolPool;
    private TypeCache typeCache;
    private EscapeEntryCache escapeEntryCache;

    public CompilationSession() {
        this.diagnostics = null;
        this.currentFile = "<none>";
        this.symbolPool = new SymbolPool();
        this.typeCache = new TypeCache();
        this.escapeEntryCache = new EscapeEntryCache();
    }

    public DiagnosticSink getDiagnosticSink() {
        return diagnostics;
    }

    public void setDiagnosticSink(DiagnosticSink diagnostics) {
        this.diagnostics = diagnostics;
    }

    public void addDiagnostic(Diagnostic d) {
        this.diagnostics.addDiagnostic(d);
    }

    public boolean hasAnyErrors() {
        return this.diagnostics.hasAnyErrors();
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public TypeCache getTypeCache() {
        return typeCache;
    }

    public EscapeEntryCache getEscapeEntryCache() {
        return escapeEntryCache;
    }

    public Symbol intern(String str) {
        return this.symbolPool.getSymbol(str);
    }
}
