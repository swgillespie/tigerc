package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;

/**
 * Created by sean on 3/4/15.
 */
public class SemanticAnalysisPassFactory {
    public static CompilationPass<AstNode, AstNode> CreateSemanticAnalysisPass() {
        return new SemanticAnalysisPass();
    }
}
