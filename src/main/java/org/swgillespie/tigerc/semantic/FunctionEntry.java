package org.swgillespie.tigerc.semantic;

import java.util.List;

/**
 * Created by sean on 3/4/15.
 */
public final class FunctionEntry extends Entry {
    private List<Type> formalParameters;
    private Type returnType;

    public FunctionEntry(List<Type> formalParameters, Type returnType) {
        this.formalParameters = formalParameters;
        this.returnType = returnType;
    }

    public List<Type> getFormalParameters() {
        return formalParameters;
    }

    public void setFormalParameters(List<Type> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "FunctionEntry{" +
                "formalParameters=" + formalParameters +
                ", returnType=" + returnType +
                '}';
    }
}
