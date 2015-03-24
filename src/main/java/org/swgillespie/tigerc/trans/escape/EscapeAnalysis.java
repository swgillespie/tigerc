package org.swgillespie.tigerc.trans.escape;

import java.util.List;
import java.util.stream.Collectors;

import org.swgillespie.tigerc.ast.*;
import org.swgillespie.tigerc.common.BaseAstVisitor;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.common.Symbol;

/**
 * EscapeAnalysis is an AST visitor that performs escape analysis on parameters
 * and variable declarations that occur throughout the course of a Tiger program.
 * A variable "escapes" if it is passed by reference, it is required to be an lvalue
 * by a language construct (i.e. the & operator in C/C++), or it is accessed from a nested
 * function (Andrew W. Appel, 133). Since Tiger passes by value and doesn't have an & operator,
 * our escape analysis pass only needs to check and see if a variable or function parameter is
 * referenced by a function that is nested deeper than the scope in which it is defined.
 */
final class EscapeAnalysis extends BaseAstVisitor {
    private int depth;
    private EscapeTable table;
    private CompilationSession session;

    public EscapeAnalysis(CompilationSession session) {
        this.session = session;
        this.depth = 0;
        this.table = new EscapeTable();
    }

    @Override
    public void enter(FunctionDeclarationNode node) {
        List<VariableEscapeEntry> parameters = node.getParameters()
                .stream()
                .map(f -> new VariableEscapeEntry(depth + 1))
                .collect(Collectors.toList());
        FunctionEscapeEntry funcEntry = new FunctionEscapeEntry(depth, parameters);
        table.insert(session.intern(node.getName()), funcEntry);
        session.getEscapeEntryCache().put(node, funcEntry);
        table.enterScope();
        List<Symbol> paramNames = node.getParameters()
                .stream()
                .map(i -> session.intern(i.getName()))
                .collect(Collectors.toList());
        for (int i = 0; i < parameters.size(); i++) {
            table.insert(paramNames.get(i), parameters.get(i));
        }
        depth++;
    }

    @Override
    public void exit(FunctionDeclarationNode node) {
        table.exitScope();
        depth--;
    }

    @Override
    public void enter(VariableDeclarationNode node) {
        VariableEscapeEntry entry = new VariableEscapeEntry(depth);
        table.insert(session.intern(node.getName()), entry);
        session.getEscapeEntryCache().put(node, entry);
    }

    @Override
    public void enter(IdentifierNode node) {
        EscapeEntry entry = table.query(session.intern(node.getIdentifier()));
        CompilerAssert.check(entry instanceof VariableEscapeEntry, "identifier nodes should not reference functions, " +
                "should have been caught by semantic analysis?");
        if (entry.getDepth() < depth) {
            ((VariableEscapeEntry)entry).setHasEscaped(true);
        }
    }
}
