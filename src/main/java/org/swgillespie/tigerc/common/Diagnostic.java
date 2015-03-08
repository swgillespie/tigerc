package org.swgillespie.tigerc.common;

import org.swgillespie.tigerc.ast.TextSpan;

/**
 * Created by sean on 3/2/15.
 */
public class Diagnostic {
    private TextSpan span;
    private Severity severity;
    private String message;
    private String file;

    public Diagnostic(TextSpan span, Severity severity, String message, String file) {
        this.span = span;
        this.severity = severity;
        this.message = message;
        this.file = file;
    }

    public TextSpan getSpan() {
        return span;
    }

    public void setSpan(TextSpan span) {
        this.span = span;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Diagnostic{" +
                "span=" + span +
                ", severity=" + severity +
                ", message='" + message + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
