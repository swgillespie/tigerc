package org.swgillespie.tigerc.trans.ir;

import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public final class IRCall extends IRExpression {
    private IRExpression function;
    private List<IRExpression> arguments;

    public IRCall(IRExpression function, List<IRExpression> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public IRExpression getFunction() {
        return function;
    }

    public List<IRExpression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "IRCall{" +
                "function=" + function +
                ", arguments=" + arguments +
                '}';
    }
}
