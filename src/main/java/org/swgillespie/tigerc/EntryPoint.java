package org.swgillespie.tigerc;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.ast.ExpressionNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.DefaultDiagnosticSink;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.parser.ParseCompilationPassFactory;
import org.swgillespie.tigerc.semantic.SemanticAnalysisPassFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by sean on 2/27/15.
 */
public class EntryPoint {
    private static final String PROMPT = ">> ";

    public static void main(String[] args) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        CompilationSession session = new CompilationSession();
        DefaultDiagnosticSink sink = new DefaultDiagnosticSink();
        session.setDiagnosticSink(sink);
        session.setCurrentFile("<stdin>");
        try {
            while (true) {
                System.out.print(PROMPT);
                String input = stdin.readLine();
                CompilationPass<String, AstNode> pass = ParseCompilationPassFactory.CreateStringPass();
                CompilationPass<AstNode, AstNode> pass2 = SemanticAnalysisPassFactory.CreateSemanticAnalysisPass();
                AstNode root = pass.runPass(session, input);
                if (root != null) {
                    root = pass2.runPass(session, root);
                }
                System.out.println(root);
                if (session.hasAnyErrors()) {
                    System.out.println("there were errors: ");
                    for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
                        System.out.println(d);
                    }
                } else {
                    if (root instanceof ExpressionNode) {
                        System.out.println("type: " + session.getTypeCache().get((ExpressionNode)root));
                    }
                }
                session.getDiagnosticSink().clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
