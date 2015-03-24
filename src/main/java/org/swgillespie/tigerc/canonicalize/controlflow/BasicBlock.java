package org.swgillespie.tigerc.canonicalize.controlflow;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.ir.IRExpression;
import org.swgillespie.tigerc.trans.ir.IRJump;
import org.swgillespie.tigerc.trans.ir.IRName;
import org.swgillespie.tigerc.trans.ir.IRStatement;
import java.util.List;

/**
 * Created by sean on 3/14/15.
 */
public class BasicBlock {
    private TempLabel name;
    private List<IRStatement> statements;

    public BasicBlock(TempLabel name, List<IRStatement> statements) {
        this.name = name;
        this.statements = statements;
    }

    public List<IRStatement> getStatements() {
        return statements;
    }

    public TempLabel getName() {
        return name;
    }

    public boolean isExitBlock() {
        CompilerAssert.check(statements.size() > 0, "empty block?");
        IRStatement finalStmt = statements.get(statements.size() - 1);
        // i wish I had pattern matching :(
        if (finalStmt instanceof IRJump) {
            IRExpression target = ((IRJump) finalStmt).getTarget();
            if (target instanceof IRName) {
                TempLabel targetLabel = ((IRName) target).getName();
                if (targetLabel.getName().equals("done")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "BasicBlock{" +
                "name=" + name +
                ", statements=" + statements +
                '}';
    }
}
