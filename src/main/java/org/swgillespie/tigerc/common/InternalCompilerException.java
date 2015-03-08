package org.swgillespie.tigerc.common;

/**
 * Created by sean on 3/2/15.
 */
public class InternalCompilerException extends RuntimeException {
    public InternalCompilerException(String s) {
        super("internal compiler error: " + s);
    }
}
