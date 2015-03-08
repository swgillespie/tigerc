package org.swgillespie.tigerc.common;

/**
 * Created by sean on 3/4/15.
 */
public class CompilerAssert {
    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new InternalCompilerException(message);
        }
    }

    public static void panic(String message) {
        throw new InternalCompilerException(message);
    }
}
