package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/4/15.
 */
public class ErrorType extends Type {
    public static final ErrorType Instance = new ErrorType();

    @Override
    public boolean isEquivalent(Type other) {
        return true;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isRecord() {
        return true;
    }
}
