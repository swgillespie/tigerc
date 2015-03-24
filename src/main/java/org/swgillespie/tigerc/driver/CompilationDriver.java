package org.swgillespie.tigerc.driver;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.backend.instructionselection.InstructionSelectionPassFactory;
import org.swgillespie.tigerc.backend.regalloc.liveness.controlflow.ControlFlowPass;
import org.swgillespie.tigerc.canonicalize.canontrans.CanonicalMutationPass;
import org.swgillespie.tigerc.canonicalize.controlflow.BlockBuildingPass;
import org.swgillespie.tigerc.canonicalize.refelimination.LabelReferenceEliminationPass;
import org.swgillespie.tigerc.canonicalize.tracegeneration.TracePass;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.optimization.constantfold.ConstantFoldingPass;
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
        CompilationPass<TransFragments, TransFragments> pass5 = new LabelReferenceEliminationPass();
        CompilationPass<TransFragments, TransFragments> pass6 = new CanonicalMutationPass();
        CompilationPass<TransFragments, TransFragments> pass7 = new BlockBuildingPass();
        CompilationPass<TransFragments, TransFragments> pass8 = new ConstantFoldingPass();
        CompilationPass<TransFragments, TransFragments> pass9 = new TracePass();
        CompilationPass<TransFragments, TransFragments> pass10 = InstructionSelectionPassFactory.createPass(session.getTarget());
        CompilationPass<TransFragments, TransFragments> pass11 = new ControlFlowPass();


        AstNode node = runPass(session, input, pass1);
        if (session.hasAnyErrors()) {
            return null;
        }

        node = runPass(session, node, pass2);
        if (session.hasAnyErrors()) {
            return null;
        }

        // after this point, no more errors will be generated
        node = runPass(session, node, pass3);
        TransFragments fragments = runPass(session, node, pass4);
        fragments = runPass(session, fragments, pass5);
        fragments = runPass(session, fragments, pass6);
        fragments = runPass(session, fragments, pass7);
        fragments = runPass(session, fragments, pass8);
        fragments = runPass(session, fragments, pass9);
        fragments = runPass(session, fragments, pass10);
        fragments = runPass(session, fragments, pass11);
        return fragments;
    }

    public static <TIn, TOut> TOut runPass(CompilationSession session, TIn input, CompilationPass<TIn, TOut> pass) {
        long startTimeNano = System.nanoTime();
        TOut result = pass.runPass(session, input);
        long stopTimeNano = System.nanoTime();
        double ms = (stopTimeNano - startTimeNano) / (double)1000000;
        System.out.println(String.format("%-35s %-10f ms", pass.getName(), ms));
        return result;
    }
}
