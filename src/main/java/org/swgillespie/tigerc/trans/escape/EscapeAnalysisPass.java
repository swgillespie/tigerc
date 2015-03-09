package org.swgillespie.tigerc.trans.escape;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;

/**
 * Created by sean on 3/8/15.
 */
public class EscapeAnalysisPass implements CompilationPass<AstNode, AstNode> {
    @Override
    public String getName() {
        return "escape analysis";
    }

    @Override
    public AstNode runPass(CompilationSession session, AstNode astNode) {
        EscapeAnalysis sweeper = new EscapeAnalysis(session);
        astNode.accept(sweeper);
        return astNode;
    }
}
