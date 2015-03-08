package org.swgillespie.tigerc.semantic;

/**
 * Created by sean on 3/4/15.
 */
public class VariableEntry extends Entry {
    private Type type;
    private boolean isLoopVariable;

    public VariableEntry(Type type) {
        this.type = type;
        this.isLoopVariable = false;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLoopVariable() {
        return isLoopVariable;
    }

    public void setLoopVariable(boolean isLoopVariable) {
        this.isLoopVariable = isLoopVariable;
    }

    @Override
    public String toString() {
        return "VariableEntry{" +
                "type=" + type +
                '}';
    }
}
