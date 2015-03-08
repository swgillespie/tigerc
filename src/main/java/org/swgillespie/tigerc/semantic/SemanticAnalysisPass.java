package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;

/**
 * Created by sean on 3/4/15.
 */
public class SemanticAnalysisPass implements CompilationPass<AstNode, AstNode> {
    @Override
    public String getName() {
        return "Semantic analysis pass";
    }

    @Override
    public AstNode runPass(CompilationSession session, AstNode astNode) {
        TypecheckVisitor visitor = new TypecheckVisitor(session);
        astNode.accept(visitor);
        return astNode;
    }
}
