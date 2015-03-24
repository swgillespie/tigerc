package org.swgillespie.tigerc.backend.instructionselection;

import org.swgillespie.tigerc.trans.TempLabel;

import java.util.List;

/**
 * Created by sean on 3/18/15.
 */
public class AssemblyTargets {
    private List<TempLabel> targets;

    public AssemblyTargets(List<TempLabel> targets) {
        this.targets = targets;
    }

    public List<TempLabel> getTargets() {
        return targets;
    }
}
