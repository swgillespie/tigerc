package org.swgillespie.tigerc.canonicalize.canontrans;

import org.swgillespie.tigerc.trans.ir.IRExpression;

/**
 * Created by sean on 3/12/15.
 */
public class ExpressionReference {
    private IRExpression ref;

    public ExpressionReference(IRExpression ref) {
        this.ref = ref;
    }

    public IRExpression getRef() {
        return ref;
    }

    public void setRef(IRExpression ref) {
        this.ref = ref;
    }
}
