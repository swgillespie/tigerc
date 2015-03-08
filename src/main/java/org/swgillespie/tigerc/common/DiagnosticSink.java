package org.swgillespie.tigerc.common;

import java.util.Iterator;

/**
 * Created by sean on 3/2/15.
 */
public interface DiagnosticSink {
    void addDiagnostic(Diagnostic d);
    Iterable<Diagnostic> getDiagnostics();
    void clear();
    boolean hasAnyErrors();
}
