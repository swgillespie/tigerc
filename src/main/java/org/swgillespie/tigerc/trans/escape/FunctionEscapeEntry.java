package org.swgillespie.tigerc.trans.escape;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/8/15.
 */
public final class FunctionEscapeEntry extends EscapeEntry {
    private List<VariableEscapeEntry> parameters;

    public FunctionEscapeEntry(int depth, List<VariableEscapeEntry> parameters) {
        super(depth);
        this.parameters = parameters;
    }

    public List<VariableEscapeEntry> getParameters() {
        return parameters;
    }

    public List<Boolean> parameterEscapeProfile() {
        return this.parameters.stream()
                .map(VariableEscapeEntry::hasEscaped)
                .collect(Collectors.toList());
    }
}
