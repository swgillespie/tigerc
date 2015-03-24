package org.swgillespie.tigerc.canonicalize.controlflow;

import org.swgillespie.tigerc.trans.ir.IRStatement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/16/15.
 */
public class BasicBlocks {
    private Map<String, BasicBlock> blocks;

    public BasicBlocks() {
        this.blocks = new HashMap<>();
    }

    public void addBlock(String labelName, BasicBlock block) {
        this.blocks.put(labelName, block);
    }

    public BasicBlock getBlock(String labelName) {
        return this.blocks.get(labelName);
    }

    public Collection<BasicBlock> getBlocks() {
        return this.blocks.values();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (BasicBlock block : blocks.values()) {
            builder.append(block.getName().getName()).append(":\n");
            for (IRStatement statement : block.getStatements()) {
                builder.append("\t").append(statement).append("\n");
            }
        }
        return builder.toString();
    }
}
