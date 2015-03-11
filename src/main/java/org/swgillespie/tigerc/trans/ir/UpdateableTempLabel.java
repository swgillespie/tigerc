package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/8/15.
 */
public class UpdateableTempLabel extends TempLabel {
    private TempLabel label;

    public UpdateableTempLabel() {
        super("<temporary label>");
    }

    public TempLabel getLabel() {
        return label;
    }

    public void setLabel(TempLabel label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "UpdateableTempLabel{" +
                "label=" + label +
                '}';
    }
}
