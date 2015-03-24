package org.swgillespie.tigerc;

import org.swgillespie.tigerc.backend.instructionselection.Instruction;
import org.swgillespie.tigerc.backend.instructionselection.LabelInstruction;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.DefaultDiagnosticSink;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.driver.CompilationDriver;
import org.swgillespie.tigerc.trans.Target;
import org.swgillespie.tigerc.trans.treebuild.Fragment;
import org.swgillespie.tigerc.trans.treebuild.ProcFragment;
import org.swgillespie.tigerc.trans.treebuild.TransFragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

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
                if (input == null) {
                    System.out.println("\nbye!");
                    break;
                }
                TransFragments frags = CompilationDriver.runPipeline(session, input);
                if (session.hasAnyErrors()) {
                    System.out.println("there were errors: ");
                    for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
                        System.out.println(d);
                    }
                } else {
                    for (Fragment f : frags.getFragments()) {
                        if (f instanceof ProcFragment) {
                            /*
                            BasicBlocks blocks = session.getBasicBlockCache().get((ProcFragment)f);
                            System.out.println("blocks:\n " + blocks);
                            System.out.println("flattened: ");
                            List<IRStatement> body = session.getProcBodyCache().get((ProcFragment)f);
                            for (IRStatement stmt : body) {
                                if (stmt instanceof IRLabel) {
                                    System.out.println(stmt);
                                } else {
                                    System.out.println("\t" + stmt);
                                }
                            }
                            System.out.println("------------");
                            */
                            List<Instruction> instrs = session.getInstructionCache().get((ProcFragment)f);
                            System.out.println("instructions:\n");
                            for (Instruction ir : instrs) {
                                if (ir instanceof LabelInstruction) {
                                    System.out.println(ir + ":");
                                } else {
                                    System.out.println("\t" + ir);
                                }
                            }
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
