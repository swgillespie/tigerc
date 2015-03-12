package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;

/**
 * Created by sean on 3/10/15.
 */
public class IRTransPass implements CompilationPass<AstNode, TransFragments> {
    @Override
    public String getName() {
        return "ir translation";
    }

    @Override
    public TransFragments runPass(CompilationSession session, AstNode astNode) {
        TransVisitor trans = new TransVisitor(session);
        astNode.accept(trans);
        return trans.getFragments();
    }
}
