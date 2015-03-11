package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.ir.IRTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/9/15.
 */
public final class IRTreeCache {
    private Map<AstNode, IRTree> trees;

    public IRTreeCache() {
        this.trees = new HashMap<>();
    }

    public void put(AstNode node, IRTree tree) {
        this.trees.put(node, tree);
    }

    public IRTree get(AstNode node) {
        IRTree value = this.trees.get(node);
        CompilerAssert.check(value != null, "IR tree for node was null: " + node);
        return value;
    }
}
