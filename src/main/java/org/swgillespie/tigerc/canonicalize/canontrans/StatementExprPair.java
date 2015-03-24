package org.swgillespie.tigerc.canonicalize.canontrans;

import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRStatement;

/**
 * Created by sean on 3/12/15.
 */
public class StatementExprPair {
    private IRStatement statement;
    private IRExpression expression;

    public StatementExprPair(IRStatement statement, IRExpression expression) {
        this.statement = statement;
        this.expression = expression;
    }

    public IRStatement getStatement() {
        return statement;
    }

    public IRExpression getExpression() {
        return expression;
    }
}
