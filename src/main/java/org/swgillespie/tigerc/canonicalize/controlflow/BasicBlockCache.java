package org.swgillespie.tigerc.canonicalize.controlflow;

import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/16/15.
 */
public class BasicBlockCache {
    private Map<ProcFragment, BasicBlocks> blocks;

    public BasicBlockCache() {
        this.blocks = new HashMap<>();
    }

    public void put(ProcFragment fragment, BasicBlocks blocks) {
        this.blocks.put(fragment, blocks);
    }

    public BasicBlocks get(ProcFragment fragment) {
        return this.blocks.get(fragment);
    }
}
