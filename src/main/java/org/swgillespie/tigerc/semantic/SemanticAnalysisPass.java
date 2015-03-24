package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.ast.ExpressionNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.common.Severity;

/**
 * Created by sean on 3/4/15.
 */
public class SemanticAnalysisPass implements CompilationPass<AstNode, AstNode> {
    @Override
    public String getName() {
        return "semantic analysis";
    }

    @Override
    public AstNode runPass(CompilationSession session, AstNode astNode) {
        TypecheckVisitor visitor = new TypecheckVisitor(session);
        astNode.accept(visitor);
        Type toplevelType = session.getTypeCache().get((ExpressionNode)astNode);
        if (!toplevelType.isEquivalent(IntegerType.Instance) && !toplevelType.isEquivalent(VoidType.Instance)) {
            Diagnostic d = new Diagnostic(astNode.getSpan(), Severity.Error,
                    "top-level expression must have type int or void, got " + toplevelType,
                    session.getCurrentFile());
            session.addDiagnostic(d);
        }
        return astNode;
    }
}
