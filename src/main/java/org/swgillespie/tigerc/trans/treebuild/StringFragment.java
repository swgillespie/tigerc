package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/11/15.
 */
public class StringFragment extends Fragment {
    private TempLabel label;
    private String str;

    public StringFragment(TempLabel label, String str) {
        this.label = label;
        this.str = str;
    }

    public TempLabel getLabel() {
        return label;
    }

    public String getStr() {
        return str;
    }

    @Override
    public String toString() {
        return "StringFragment{" +
                "label=" + label +
                ", str='" + str + '\'' +
                '}';
    }
}
