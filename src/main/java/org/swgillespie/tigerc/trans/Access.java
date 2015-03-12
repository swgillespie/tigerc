package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRExpressionTree;
import org.swgillespie.tigerc.trans.ir.IRTemp;
import org.swgillespie.tigerc.trans.ir.IRTree;

/**
 * Created by sean on 3/8/15.
 */
public final class Access {
    private Level level;
    private FrameAccess access;

    public Access(Level level, FrameAccess access) {
        this.level = level;
        this.access = access;
    }

    public Level getLevel() {
        return level;
    }

    public FrameAccess getAccess() {
        return access;
    }

    /**
     * simpleVar translates an Access and a Level to an IRExpressionTree that will perform
     * the necessary static link traversals.
     * @param otherLevel The level in which this variable was referenced
     * @param factory A temporary factory to use when creating the expression tree
     * @return An IRExpressionTree that will perform the required static link traversals.
     */
    public IRExpressionTree simpleVar(Level otherLevel, TempFactory factory) {
        // otherLevel is a level that is either deeper or
        // at the same level as the current level.
        Level cursor = otherLevel;
        // to access the static link from the current level, we can use the frame pointer
        // of the current frame.
        IRExpression staticLinkExpression = new IRTemp(level.getFrame().framePointer());
        while (otherLevel != level) {
            // for every level between us and the level that this var was defined, we have to
            // retrieve the static link and use it to find the static link for the /next/ frame.
            CompilerAssert.check(otherLevel.getFormals().size() > 0, "frame formals list was empty");
            Access staticLink = otherLevel.getFormals().get(0);
            staticLinkExpression = staticLink
                    .getAccess()
                    .toExpression(staticLinkExpression, factory);
            cursor = cursor.getParent();
        }
        // the result is an expression that will traverse the static links until it reaches the correct level
        // where the variable is defined. From there, we can use the frame access to determine how to access
        // the var based on its stack frame. The static link expression may or may not be ignored depending
        // on where the target's FrameAccess class chooses to allocate the var.
        // In Mips, for example, staticLinkExpression will be ignored completely if the variable
        // doesn't escape (i.e. it's stored in a register)
        IRExpression accessExpr = access.toExpression(staticLinkExpression, factory);
        return new IRExpressionTree(accessExpr);
    }
}
