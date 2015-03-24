package org.swgillespie.tigerc.canonicalize.tracegeneration;

import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlock;
import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlocks;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.ir.*;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/17/15.
 */
public class TraceBuilder {
    private CompilationSession session;

    public TraceBuilder(CompilationSession session) {
        this.session = session;
    }

    /**
     * This is a really naive trace-building algorithm that basically just
     * does a depth-first traversal of the control flow graph. We can do
     * way better than this. In particular, the epilog of a function is bound
     * to end up in the middle of the trace, which just feels /wrong/ to me.
     *
     * We could make this better in a lot of ways. We could attempt to minimize
     * the number of jumps (since jumps to adjacent blocks get eliminated), attempt
     * to keep loops in the same trace, etc.
     * @param proc The proc fragment to build a trace for
     * @return A list of traces that cover the proc.
     */
    public List<Trace> buildTraces(ProcFragment proc) {
        BasicBlocks blocks = session.getBasicBlockCache().get(proc);
        Set<BasicBlock> markedBlocks = new HashSet<>();
        List<BasicBlock> blockList = new ArrayList<>();
        List<Trace> traces = new ArrayList<>();
        blockList.addAll(blocks.getBlocks());
        while (!blockList.isEmpty()) {
            List<BasicBlock> currentTrace = new ArrayList<>();
            BasicBlock currentBlock = blockList.remove(0);
            while (!markedBlocks.contains(currentBlock)) {
                markedBlocks.add(currentBlock);
                currentTrace.add(currentBlock);
                for (BasicBlock successor : getSuccessors(blocks, currentBlock)) {
                    if (!markedBlocks.contains(successor)) {
                        currentBlock = successor;
                        break;
                    }
                }
            }
            // all of currentBlock's successors have been marked, so end this trace
            // and start a new one.
            Trace trace = new Trace(currentTrace);
            traces.add(trace);
        }
        return traces;
    }

    public List<IRStatement> flattenTrace(List<Trace> traces) {
        List<IRStatement> statements = new ArrayList<>();
        for (Trace trace : traces) {
            for (BasicBlock block : trace.getBlocks()) {
                statements.add(new IRLabel(block.getName()));
                statements.addAll(block.getStatements());
            }
        }
        this.collectCjumps(statements);
        this.removeUselessJumps(statements);
        return statements;
    }

    /**
     * Ensures that all cjumps are followed immediately by their false label
     * for ease of code generation. This function modifies the passed-in statements
     * list in-place and returns void.
     * @param statements The flattened list of statements to be mutated.
     */
    private void collectCjumps(List<IRStatement> statements) {
        for (int i = 0; i < statements.size() - 1; i++) {
            IRStatement stmt = statements.get(i);
            if (stmt instanceof IRConditionalJump) {
                IRConditionalJump cond = (IRConditionalJump) stmt;
                // three rules here:
                // 1) all cjumps followed immediately by their false target label are left alone,
                // 2) all cjumps followed by their true target label are negated and labels are swapped,
                // 3) all cjumps followed by neither label get modified to:
                //   CJUMP(cond, a, b, lt, lf')
                // lf':
                //   JUMP lf
                // ultimately, every CJUMP gets followed by its false target label.
                IRStatement next = statements.get(i + 1);
                if (next instanceof IRLabel) {
                    IRLabel label = (IRLabel) next;
                    if (label.getLabel() == cond.getFalseTarget()) {
                        // rule 1
                        continue;
                    }
                    if (label.getLabel() == cond.getTrueTarget()) {
                        // rule 2, invert the condition and swap the labels
                        IRConditionalJump newCond = new IRConditionalJump(cond.getFirstExpr(),
                                cond.getSecondExpr(),
                                IRRelationalOp.negate(cond.getRelationalOp()),
                                cond.getFalseTarget(),
                                cond.getTrueTarget());
                        statements.set(i, newCond);
                        continue;
                    }
                }
                // otherwise, we need to apply rule 3.
                TempLabel newTarget = session.getTempFactory().newLabel();
                IRConditionalJump newCond = new IRConditionalJump(cond.getFirstExpr(),
                        cond.getSecondExpr(),
                        cond.getRelationalOp(),
                        cond.getTrueTarget(),
                        newTarget);
                IRLabel newFalseJump = new IRLabel(newTarget);
                IRJump newJump = new IRJump(cond.getFalseTarget());
                // we need to be a little careful inserting into this list.
                // the statement that needs to be replaced is located at index i
                statements.set(i, newCond);
                // next, we need to insert the new false jump label at i + 1
                statements.add(i + 1, newFalseJump);
                // finally, the new jump is located at i + 2
                statements.add(i + 2, newJump);
                // add 2 to the loop index to avoid iterating over these new statements
                i = i + 2;
            }
        }
    }

    /**
     * Removes all jumps to a label that immediately follows it.
     * @param statements The list of statements to operate upon. This list is
     *                   mutated in-place.
     */
    private void removeUselessJumps(List<IRStatement> statements) {
        for (int i = 0; i < statements.size() - 1; i++) {
            IRStatement stmt = statements.get(i);
            if (!(stmt instanceof IRJump) || !(((IRJump) stmt).getTarget() instanceof IRName)) {
                continue;
            }
            TempLabel target = ((IRName) ((IRJump) stmt).getTarget()).getName();
            IRStatement nextStmt = statements.get(i + 1);
            if (nextStmt instanceof IRLabel && ((IRLabel) nextStmt).getLabel() == target) {
                statements.remove(i);
            }
        }
    }

    public static List<BasicBlock> getSuccessors(BasicBlocks allBlocks, BasicBlock thisBlock) {
        // basic blocks end in either a JUMP or a CJUMP.
        IRStatement finalStmt = thisBlock.getStatements().get(thisBlock.getStatements().size() - 1);
        List<TempLabel> targets;
        if (finalStmt instanceof IRJump) {
            targets = ((IRJump) finalStmt).getPossibleTargetLabels();
        } else if (finalStmt instanceof IRConditionalJump) {
            targets = Arrays.asList(((IRConditionalJump) finalStmt).getTrueTarget(),
                    ((IRConditionalJump) finalStmt).getFalseTarget());
        } else {
            CompilerAssert.panic("basic block ended in something other than JUMP or CJUMP?");
            return null;
        }
        return targets.stream()
                .map(t -> allBlocks.getBlock(t.getName()))
                .filter(t -> t != null) // "done" doesn't have a block since it doesn't really exist
                .collect(Collectors.toList());
    }
}
