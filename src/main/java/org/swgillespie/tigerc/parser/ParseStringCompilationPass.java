package org.swgillespie.tigerc.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.parser.TigerLexer;
import org.swgillespie.tigerc.parser.TigerParser;

/**
 * Created by sean on 3/2/15.
 */
class ParseStringCompilationPass implements CompilationPass<String, AstNode> {
    private static final String PASS_NAME = "ast generation";

    @Override
    public String getName() {
        return PASS_NAME;
    }

    @Override
    public AstNode runPass(CompilationSession session, String s) {
        TigerLexer lexer = new TigerLexer(new ANTLRInputStream(s));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener(session));
        if (session.hasAnyErrors()) {
            return null;
        }
        CommonTokenStream cts = new CommonTokenStream(lexer);
        TigerParser parser = new TigerParser(cts);
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
