package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.common.Symbol;

/**
 * Created by sean on 3/6/15.
 */
public final class RecordField {
    public final Symbol name;
    public final Type type;

    public RecordField(Symbol name, Type type) {
        this.name = name;
        this.type = type;
    }
}

