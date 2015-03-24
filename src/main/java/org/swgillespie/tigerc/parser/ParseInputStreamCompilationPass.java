package org.swgillespie.tigerc.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.common.Severity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sean on 3/2/15.
 */
class ParseInputStreamCompilationPass implements CompilationPass<InputStream, AstNode> {
    private static final String PASS_NAME = "ast generation";

    @Override
    public String getName() {
        return PASS_NAME;
    }

    @Override
    public AstNode runPass(CompilationSession session, InputStream inputStream) {
        org.swgillespie.tigerc.parser.TigerLexer lexer = null;
        try {
            lexer = new org.swgillespie.tigerc.parser.TigerLexer(new ANTLRInputStream(inputStream));
        } catch (IOException exn) {
            session.addDiagnostic(new Diagnostic(null, Severity.Fatal, "failed to initialize input stream", session.getCurrentFile()));
            return null;
        }
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener(session));
        if (session.hasAnyErrors()) {
            return null;
        }
        CommonTokenStream cts = new CommonTokenStream(lexer);
        org.swgillespie.tigerc.parser.TigerParser parser = new org.swgillespie.tigerc.parser.TigerParser(cts);
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener(session));
        ParseTree tree = parser.program();
        if (session.hasAnyErrors()) {
            return null;
        }
        TreeBuilder builder = new TreeBuilder();
        return builder.buildAst(tree);
    }
}
