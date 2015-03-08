package org.swgillespie.tigerc.parser;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.swgillespie.tigerc.ast.TextPosition;
import org.swgillespie.tigerc.ast.TextSpan;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.Diagnostic;
import org.swgillespie.tigerc.common.DiagnosticSink;
import org.swgillespie.tigerc.common.Severity;

import java.util.BitSet;

/**
 * Created by sean on 3/2/15.
 */
class LexerErrorListener implements ANTLRErrorListener {
    private CompilationSession session;

    public LexerErrorListener(CompilationSession session) {
        this.session = session;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int col, String s, RecognitionException e) {
        TextSpan span = new TextSpan(new TextPosition(line, col), new TextPosition(line, col));
        Diagnostic d = new Diagnostic(span, Severity.Error, "lexer error: " + s, session.getCurrentFile());
        session.addDiagnostic(d);
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // not used by lexers
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // not used by lexers
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        // not used by lexers
    }
}
