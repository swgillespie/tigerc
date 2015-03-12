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
import org.swgillespie.tigerc.trans.treebuild.Fragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

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
                TransFragments frags = CompilationDriver.runPipeline(session, input);
                if (session.hasAnyErrors()) {
                    System.out.println("there were errors: ");
                    for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
                        System.out.println(d);
                    }
                } else {
                    for (Fragment f : frags.getFragments()) {
                        System.out.println("fragment: " + f);
                    }
                }
                session.getDiagnosticSink().clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
