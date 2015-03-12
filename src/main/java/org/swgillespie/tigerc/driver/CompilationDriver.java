package org.swgillespie.tigerc.driver;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.parser.ParseCompilationPassFactory;
import org.swgillespie.tigerc.semantic.SemanticAnalysisPassFactory;
import org.swgillespie.tigerc.trans.escape.EscapeAnalysisPass;
import org.swgillespie.tigerc.trans.treebuild.IRTransPass;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

/**
 * Created by sean on 3/2/15.
 */
public class CompilationDriver {
    public static TransFragments runPipeline(CompilationSession session, String input) {
        CompilationPass<String, AstNode> pass1 = ParseCompilationPassFactory.CreateStringPass();
        CompilationPass<AstNode, AstNode> pass2 = SemanticAnalysisPassFactory.CreateSemanticAnalysisPass();
        CompilationPass<AstNode, AstNode> pass3 = new EscapeAnalysisPass();
        CompilationPass<AstNode, TransFragments> pass4 = new IRTransPass();

        AstNode node = pass1.runPass(session, input);
        if (session.hasAnyErrors()) {
            return null;
        }

        node = pass2.runPass(session, node);
        if (session.hasAnyErrors()) {
            return null;
        }

        // after this point, no more errors will be generated
        node = pass3.runPass(session, node);
        TransFragments fragments = pass4.runPass(session, node);
        return fragments;
    }
}
