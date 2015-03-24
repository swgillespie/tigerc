package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sean on 3/8/15.
 */
public final class IRConditionalJump extends IRStatement {
    private IRExpression firstExpr;
    private IRExpression secondExpr;
    private IRRelationalOp relationalOp;
    private TempLabel trueTarget;
    private TempLabel falseTarget;

    public IRConditionalJump(IRExpression firstExpr, IRExpression secondExpr, IRRelationalOp relationalOp, TempLabel trueTarget, TempLabel falseTarget) {
        this.firstExpr = firstExpr;
        this.secondExpr = secondExpr;
        this.relationalOp = relationalOp;
        this.trueTarget = trueTarget;
        this.falseTarget = falseTarget;
    }

    public IRExpression getFirstExpr() {
        return firstExpr;
    }

    public IRExpression getSecondExpr() {
        return secondExpr;
    }

    public IRRelationalOp getRelationalOp() {
        return relationalOp;
    }

    public TempLabel getTrueTarget() {
        return trueTarget;
    }

    public TempLabel getFalseTarget() {
        return falseTarget;
    }

    public void setFirstExpr(IRExpression firstExpr) {
        this.firstExpr = firstExpr;
    }

    public void setSecondExpr(IRExpression secondExpr) {
        this.secondExpr = secondExpr;
    }

    public void setRelationalOp(IRRelationalOp relationalOp) {
        this.relationalOp = relationalOp;
    }

    public void setTrueTarget(TempLabel trueTarget) {
        this.trueTarget = trueTarget;
    }

    public void setFalseTarget(TempLabel falseTarget) {
        this.falseTarget = falseTarget;
    }

    @Override
    public String toString() {
        return "if " + firstExpr + " " + relationalOp + " "
                + secondExpr + " jump " + trueTarget + " else jump " + falseTarget;
    }
}
