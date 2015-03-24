package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.common.CompilerAssert;

/**
 * Created by sean on 3/8/15.
 */
public enum IRRelationalOp {
    Eq,
    Neq,
    Lt,
    Gt,
    Leq,
    Geq,
    Ult,
    Ule,
    Ugt,
    Uge;

    public static IRRelationalOp negate(IRRelationalOp op) {
        switch (op) {
            case Eq:
                return Neq;
            case Neq:
                return Eq;
            case Lt:
                return Geq;
            case Gt:
                return Leq;
            case Leq:
                return Gt;
            case Geq:
                return Lt;
            case Ult:
                return Uge;
            case Ugt:
                return Ule;
            case Uge:
                return Ult;
            case Ule:
                return Ugt;
            default:
                CompilerAssert.panic("unreachable code");
                return op;
        }
    }
}
