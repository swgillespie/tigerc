package org.swgillespie.tigerc.trans.treebuild;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 3/11/15.
 */
public class TransFragments {
    private List<Fragment> fragments;

    public TransFragments() {
        this.fragments = new ArrayList<>();
    }

    public void add(Fragment fragment) {
        fragments.add(fragment);
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
}
