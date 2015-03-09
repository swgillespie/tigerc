package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/8/15.
 */
public final class IRName extends IRExpression {
    private TempLabel name;

    public IRName(TempLabel name) {
        this.name = name;
    }

    public TempLabel getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IRName{" +
                "name=" + name +
                '}';
    }
}
