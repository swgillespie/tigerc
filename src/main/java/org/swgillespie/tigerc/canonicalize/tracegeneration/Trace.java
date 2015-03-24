package org.swgillespie.tigerc.canonicalize.tracegeneration;

import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlock;

import java.util.List;

/**
 * Created by sean on 3/17/15.
 */
public class Trace {
    private List<BasicBlock> blocks;

    public Trace(List<BasicBlock> blocks) {
        this.blocks = blocks;
    }

    public List<BasicBlock> getBlocks() {
        return blocks;
    }
}
