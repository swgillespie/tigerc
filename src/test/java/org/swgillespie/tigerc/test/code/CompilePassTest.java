package org.swgillespie.tigerc.test.code;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.DefaultDiagnosticSink;
import org.swgillespie.tigerc.common.DiagnosticSink;
import org.swgillespie.tigerc.parser.ParseCompilationPassFactory;
import org.swgillespie.tigerc.semantic.SemanticAnalysisPassFactory;
import org.swgillespie.tigerc.trans.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by sean on 3/4/15.
 */
public class CompilePassTest {

    private static final String DIRECTORY = "src/test/java/org/swgillespie/tigerc/test/code/pass";

    private void compilePass(String name) {
        CompilationSession session = new CompilationSession(Target.MIPS);
        DiagnosticSink diagnosticSink = new DefaultDiagnosticSink();
        session.setDiagnosticSink(diagnosticSink);
        session.setCurrentFile(name + ".tig");

        CompilationPass<InputStream, AstNode> pass1 = ParseCompilationPassFactory.CreateInputStreamPass();
        CompilationPass<AstNode, AstNode> pass2 = SemanticAnalysisPassFactory.CreateSemanticAnalysisPass();

        try {
            AstNode ast = pass1.runPass(session, new FileInputStream(new File(DIRECTORY, name + ".tig")));
            if (ast != null) {
                ast = pass2.runPass(session, ast);
            }
            assertNotNull("failed to parse", ast);
            if (session.hasAnyErrors()) {
                fail("received errors: " + session.getDiagnosticSink().getDiagnostics());
            }
        } catch (FileNotFoundException exn) {
            fail("could not find file: " + name + ".tig. Current relative path: " + System.getProperty("user.dir"));
        }
    }

    @Test
    public void testIntLiteral() {
        compilePass("int_literal");
    }

    @Test
    public void testIdentityFunction() {
        compilePass("identity_function");
    }

    @Test
    public void testPlusOperator() {
        compilePass("plus_operator");
    }

    @Test
    public void testPrintFunction() {
        compilePass("print_function");
    }

    @Test
    public void testSquareFunction() {
        compilePass("square_function");
    }

    @Test
    public void testStringLiteral() {
        compilePass("string_literal");
    }

    @Test
    public void testVariableDefinition() {
        compilePass("variable_definition");
    }

    @Test
    public void testVariableDefinitionWithType() {
        compilePass("variable_definition_with_type");
    }

    @Test
    public void testTypeDeclaration() {
        compilePass("type_declaration");
    }

    @Test
    public void testRecursion() {
        compilePass("recursion");
    }

    @Test
    public void testRecordDefinition() {
        compilePass("record_definition");
    }

    @Test
    public void testRecordAccess() {
        compilePass("record_access");
    }

    @Test
    public void testArrayDefinition() {
        compilePass("array_definition");
    }

    @Test
    public void testArrayAccess() {
        compilePass("array_access");
    }

    @Test
    public void testUseOfNil() {
        compilePass("use_of_nil");
    }

    @Test
    public void testUseOfNilParameter() {
        compilePass("use_of_nil_parameter");
    }

}
