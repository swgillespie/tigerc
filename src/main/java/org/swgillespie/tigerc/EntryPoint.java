package org.swgillespie.tigerc;

import org.swgillespie.tigerc.ast.*;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.DefaultDiagnosticSink;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.driver.CompilationDriver;
import org.swgillespie.tigerc.parser.ParseCompilationPassFactory;
import org.swgillespie.tigerc.semantic.SemanticAnalysisPassFactory;
import org.swgillespie.tigerc.trans.Target;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by sean on 2/27/15.
 */
public class EntryPoint {
    private static final String PROMPT = ">> ";

    public static void main(String[] args) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        CompilationSession session = new CompilationSession(Target.MIPS);
        DefaultDiagnosticSink sink = new DefaultDiagnosticSink();
        session.setDiagnosticSink(sink);
        session.setCurrentFile("<stdin>");
        try {
            while (true) {
                System.out.print(PROMPT);
                String input = stdin.readLine();
                AstNode root = CompilationDriver.runPipeline(session, input);
                if (session.hasAnyErrors()) {
                    System.out.println("there were errors: ");
                    for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
                        System.out.println(d);
                    }
                } else {
                    if (root instanceof ExpressionNode) {
                        System.out.println("type: " + session.getTypeCache().get((ExpressionNode)root));
                        if (root instanceof LetExpressionNode) {
                            ((LetExpressionNode) root).getDeclarations()
                                    .stream()
                                    .filter(dec -> dec instanceof FunctionDeclarationNode)
                                    .forEach(dec -> {
                                        System.out.println("function: " + dec.getName());
                                        System.out.println("ir: " + session.getIrTreeCache().get(dec));
                                    });
                            for (ExpressionNode expr : ((LetExpressionNode) root).getBody()) {
                                System.out.println("body expr: " + session.getIrTreeCache().get(expr));
                            }
                        } else {
                            System.out.println("ir: " + session.getIrTreeCache().get(root));
                        }
                    }
                }
                session.getDiagnosticSink().clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
