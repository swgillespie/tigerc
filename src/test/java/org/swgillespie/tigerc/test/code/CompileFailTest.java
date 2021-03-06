package org.swgillespie.tigerc.test.code;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.*;
import org.swgillespie.tigerc.parser.ParseCompilationPassFactory;
import org.swgillespie.tigerc.semantic.SemanticAnalysisPassFactory;
import org.swgillespie.tigerc.trans.Target;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by sean on 3/5/15.
 */
public class CompileFailTest {
    private static final String DIRECTORY = "src/test/java/org/swgillespie/tigerc/test/code/fail";

    private void compileFail(String name) {
        CompilationSession session = new CompilationSession(Target.MIPS);
        DiagnosticSink diagnosticSink = new DefaultDiagnosticSink();
        session.setDiagnosticSink(diagnosticSink);
        session.setCurrentFile(name + ".tig");

        CompilationPass<InputStream, AstNode> pass1 = ParseCompilationPassFactory.CreateInputStreamPass();
        CompilationPass<AstNode, AstNode> pass2 = SemanticAnalysisPassFactory.CreateSemanticAnalysisPass();

        try {
            File f = new File(DIRECTORY, name + ".tig");
            List<ExpectedDiagnostic> expectedDiagnostics = identifyDiagnostics(f.toPath());
            AstNode ast = pass1.runPass(session, new FileInputStream(f));
            if (ast != null) {
                ast = pass2.runPass(session, ast);
            }
            checkDiagnostics(session, expectedDiagnostics);
        } catch (IOException exn) {
            fail("could not find file: " + name + ".tig. Current relative path: " + System.getProperty("user.dir"));
        }
    }

    private List<ExpectedDiagnostic> identifyDiagnostics(Path file) throws IOException {
        Pattern pat = Pattern.compile(".*/\\*\\s*error: (.*)\\*/.*");
        List<ExpectedDiagnostic> diagnostics = new ArrayList<>();
        int lineNumber = 1;
        for (String line : Files.readAllLines(file)) {
            Matcher m = pat.matcher(line);
            if (m.matches()) {
                String message = m.group(1);
                diagnostics.add(new ExpectedDiagnostic(lineNumber, message));
            }
            lineNumber++;
        }
        return diagnostics;
    }

    private void checkDiagnostics(CompilationSession session, List<ExpectedDiagnostic> diagnostics) {
        assertTrue("compilation was successful when it should not have been", session.hasAnyErrors());
        for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
            boolean found = false;
            for (ExpectedDiagnostic ex : diagnostics) {
                if (d.getMessage().startsWith(ex.getSubstringMessage())) {
                    if (d.getSpan().getStart().getLine() == ex.getLineNumber()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                fail("unexpected diagnostic generated by compiler: " + d);
            }
        }

        for (ExpectedDiagnostic ex : diagnostics) {
            boolean found = false;
            for (Diagnostic d : session.getDiagnosticSink().getDiagnostics()) {
                if (d.getMessage().startsWith(ex.getSubstringMessage())) {
                    if (d.getSpan().getStart().getLine() == ex.getLineNumber()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                fail("failed to find expected diagnostic: " + ex);
            }
        }
    }

    private class ExpectedDiagnostic {
        private int lineNumber;
        private String substringMessage;

        public ExpectedDiagnostic(int lineNumber, String substringMessage) {
            this.lineNumber = lineNumber;
            this.substringMessage = substringMessage;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getSubstringMessage() {
            return substringMessage;
        }

        public void setSubstringMessage(String substringMessage) {
            this.substringMessage = substringMessage;
        }

        @Override
        public String toString() {
            return "ExpectedDiagnostic{" +
                    "lineNumber=" + lineNumber +
                    ", substringMessage='" + substringMessage + '\'' +
                    '}';
        }
    }

    @Test
    public void testBadAdditionOperator() {
        compileFail("bad_addition_operator");
    }

    @Test
    public void testBadFunctionCall() {
        compileFail("bad_function_call");
    }

    @Test
    public void testBadFunctionCallInLet() {
        compileFail("bad_function_call_in_let");
    }

    @Test
    public void testBadFunctionReturnType() {
        compileFail("bad_function_return_type");
    }

    @Test
    public void testBreakOutsideOfWhile() {
        compileFail("break_outside_of_while");
    }

    @Test
    public void testCallingNonFunction() {
        compileFail("calling_non_function");
    }

    @Test
    public void testUnboundIdent() {
        compileFail("unbound_ident");
    }

    @Test
    public void testUnknownFunctionCall() {
        compileFail("unknown_function_call");
    }

    @Test
    public void testArityMismatch() {
        compileFail("arity_mismatch");
    }

    @Test
    public void testAssigningToLoopIndex() {
        compileFail("assigning_to_loop_index");
    }

    @Test
    public void testArrayIndexMismatch() {
        compileFail("array_index_mismatch");
    }

    @Test
    public void testArrayInvalidIndex() {
        compileFail("array_invalid_index");
    }

    @Test
    public void testArrayIndexTypeMismatch() {
        compileFail("array_index_type_mismatch");
    }

    @Test
    public void testNonexistentRecord() {
        compileFail("nonexistent_record");
    }

    @Test
    public void testWrongRecordType() {
        compileFail("wrong_record_type");
    }

    @Test
    public void testFieldAccessNotRecord() {
        compileFail("field_access_not_record");
    }

    @Test
    public void testUseOfNilNoType() {
        compileFail("use_of_nil_no_type");
    }

    @Test
    public void testUseOfNilNotRecord() {
        compileFail("use_of_nil_not_record");
    }
}
