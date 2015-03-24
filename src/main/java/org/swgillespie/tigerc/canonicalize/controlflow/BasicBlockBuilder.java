package org.swgillespie.tigerc.canonicalize.controlflow;

import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.ir.*;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sean on 3/16/15.
 */
public class BasicBlockBuilder {
    private CompilationSession session;
    private TempFactory tempFactory;

    public BasicBlockBuilder(CompilationSession session) {
        this.session = session;
        this.tempFactory = session.getTempFactory();
    }

    private static List<IRStatement> flatten(IRStatement seq) {
        if (seq instanceof IRSeq) {
            ArrayList<IRStatement> stmts = new ArrayList<>();
            stmts.add(((IRSeq) seq).getFirst());
            stmts.addAll(flatten(((IRSeq) seq).getSecond()));
            return stmts;
        } else {
            return Arrays.asList(seq);
        }
    }

    public void buildBlocks(ProcFragment fragment) {
        List<IRStatement> flattenedStatements = flatten(fragment.getBody());
        BasicBlocks blocks = this.buildBlocksImpl(flattenedStatements);
        session.getBasicBlockCache().put(fragment, blocks);
    }

    /**
     * This function is the implementation of a basic block creation algorithm.
     * The basic algorithm is this:
     *   1) For every statement, do the following things:
     *   2) If the current statement is a label and we have not seen any statements
     *      yet in this current block, set this label to be the name of the current block.
     *      Otherwise, if we have seen a statement in this block, then terminate the current
     *      block by adding a jump to the seen opcode, and begin creating the next block.
     *   3) If the current statement is a jump or a conditional jump, terminate this block and
     *      begin the next block. We will not know what the next block is called until we see a label.
     *   4) The final block ends in a jump to a new label called "done" representing the function epilogue.
     * @param statements The list of statements to group into blocks.
     * @return A BasicBlocks object containing all of the basic blocks.
     */
    private BasicBlocks buildBlocksImpl(List<IRStatement> statements) {
        // the very first block is the "entry" block, where control enters
        // the function.
        BasicBlocks blocks = new BasicBlocks();
        TempLabel currentLabel = tempFactory.newNamedLabel("entry");
        List<IRStatement> currentBlock = new ArrayList<>();
        for (int i = 0; i < statements.size(); i++) {
            IRStatement current = statements.get(i);
            CompilerAssert.check(!(current instanceof IRSeq), "all seqs should have been eliminated by now");
            if (current instanceof IRLabel) {
                TempLabel thisLabel = ((IRLabel) current).getLabel();
                if (!currentBlock.isEmpty()) {
                    // we need to end this block with a jump to the next block.
                    currentBlock.add(new IRJump(thisLabel));
                    BasicBlock block = new BasicBlock(currentLabel, currentBlock);
                    blocks.addBlock(currentLabel.getName(), block);
                    currentBlock = new ArrayList<>();
                }
                // whether or not we've seen anything yet, we're starting a new block
                // and we need to set the label.
                currentLabel = thisLabel;
            } else if (current instanceof IRJump || current instanceof IRConditionalJump) {
                // if we see a jump instruction, we are ending this block.
                currentBlock.add(current);
                BasicBlock block = new BasicBlock(currentLabel, currentBlock);
                blocks.addBlock(currentLabel.getName(), block);
                currentBlock = new ArrayList<>();
            } else {
                // otherwise, this is a normal statement.
                currentBlock.add(current);
            }
            // finally, if this statement would invoke the function epilogue, we branch to a "done" label
            // that represents the target-specific end of function.
            if (statements.size() == i + 1) {
                currentBlock.add(new IRJump(tempFactory.newNamedLabel("done")));
                BasicBlock block = new BasicBlock(currentLabel, currentBlock);
                blocks.addBlock(currentLabel.getName(), block);
            }
        }
        return blocks;
    }

}
