package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.ast.InfixOperator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public final class IRBinop extends IRExpression {
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

    public void setOp(InfixOperator op) {
        this.op = op;
    }

    public void setLeft(IRExpression left) {
        this.left = left;
    }

    public void setRight(IRExpression right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return op + "("  + left + ", " + right + ")";
    }
}
