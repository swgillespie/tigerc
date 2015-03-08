package org.swgillespie.tigerc.parser;

import org.swgillespie.tigerc.ast.AstNode;
import org.swgillespie.tigerc.common.CompilationPass;
import org.swgillespie.tigerc.common.InternalCompilerException;

import java.io.InputStream;

/**
 * Created by sean on 3/2/15.
 */
public class ParseCompilationPassFactory {

    public static CompilationPass<String, AstNode> CreateStringPass() {
        return new ParseStringCompilationPass();
    }

    public static CompilationPass<InputStream, AstNode> CreateInputStreamPass() {
        return new ParseInputStreamCompilationPass();
    }
}
