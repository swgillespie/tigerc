package org.swgillespie.tigerc.common;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sean on 3/2/15.
 */
public class DefaultDiagnosticSink implements DiagnosticSink {
    private ArrayList<Diagnostic> diagnostics;

    public DefaultDiagnosticSink() {
        this.diagnostics = new ArrayList<>();
    }

    @Override
    public void addDiagnostic(Diagnostic d) {
        diagnostics.add(d);
    }

    @Override
    public Iterable<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    @Override
    public void clear() {
        diagnostics.clear();
    }

    @Override
    public boolean hasAnyErrors() {
        for (Diagnostic d : diagnostics) {
            if (d.getSeverity() == Severity.Error || d.getSeverity() == Severity.Fatal) {
                return true;
            }
        }
        return false;
    }
}
