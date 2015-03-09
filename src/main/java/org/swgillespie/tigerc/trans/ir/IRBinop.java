package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.ast.InfixOperator;

/**
 * Created by sean on 3/8/15.
 */
public final class IRBinop {
    private InfixOperator op;
    private IRExpression left;
    private IRExpression right;

    public IRBinop(InfixOperator op, IRExpression left, IRExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public InfixOperator getOp() {
        return op;
    }

    public IRExpression getLeft() {
        return left;
    }

    public IRExpression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "IRBinop{" +
                "op=" + op +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
