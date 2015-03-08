package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.ast.*;
import org.swgillespie.tigerc.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/4/15.
 */
final class TypecheckVisitor extends BaseAstVisitor {
    private CompilationSession session;
    private SymbolTable table;

    private int loopDepth;

    public TypecheckVisitor(CompilationSession session) {
        this.session = session;
        this.table = new SymbolTable();
        this.initializeSymbolTable();
    }

    private void initializeSymbolTable() {
        this.table.insertType(session.intern("int"), IntegerType.Instance);
        this.table.insertType(session.intern("string"), StringType.Instance);

        this.table.insertEntry(session.intern("print"),
                new FunctionEntry(Arrays.asList(StringType.Instance), VoidType.Instance));
        this.table.insertEntry(session.intern("flush"),
                new FunctionEntry(Arrays.asList(), VoidType.Instance));
        this.table.insertEntry(session.intern("getchar"),
                new FunctionEntry(Arrays.asList(), StringType.Instance));
        this.table.insertEntry(session.intern("ord"),
                new FunctionEntry(Arrays.asList(StringType.Instance), IntegerType.Instance));
        this.table.insertEntry(session.intern("chr"),
                new FunctionEntry(Arrays.asList(IntegerType.Instance), StringType.Instance));
        this.table.insertEntry(session.intern("size"),
                new FunctionEntry(Arrays.asList(StringType.Instance), IntegerType.Instance));
        this.table.insertEntry(session.intern("substring"),
                new FunctionEntry(Arrays.asList(StringType.Instance, IntegerType.Instance, IntegerType.Instance),
                        StringType.Instance));
        this.table.insertEntry(session.intern("concat"),
                new FunctionEntry(Arrays.asList(StringType.Instance, StringType.Instance), StringType.Instance));
        this.table.insertEntry(session.intern("not"),
                new FunctionEntry(Arrays.asList(IntegerType.Instance), IntegerType.Instance));
        this.table.insertEntry(session.intern("exit"),
                new FunctionEntry(Arrays.asList(IntegerType.Instance), VoidType.Instance));
    }

    private void error(AstNode node, String message) {
        Diagnostic d = new Diagnostic(node.getSpan(), Severity.Error, message, session.getCurrentFile());
        session.addDiagnostic(d);
    }

    private boolean inLoop() {
        return loopDepth != 0;
    }

    private Type getTypeFromIdentifier(IdentifierTypeNode type) {
        Type returnType = table.queryType(session.intern(type.getName()));
        if (returnType == null) {
            this.error(type, "unknown type: " + type.getName());
            returnType = ErrorType.Instance;
        }
        return returnType;
    }

    private void checkTypes(Type target, ExpressionNode expr) {
        Type exprType = session.getTypeCache().get(expr);
        if (!target.isEquivalent(exprType)) {
            this.error(expr, "mismatched types: expected " + target + ", got " + exprType);
        }
    }

    @Override
    public void enter(FunctionDeclarationNode node) {
        Type returnType;
        if (node.hasReturnType()) {
            returnType = this.getTypeFromIdentifier(node.getReturnType());
        } else {
            returnType = VoidType.Instance;
        }

        List<Type> formalParamTypes = node.getParameters()
                .stream()
                .map(param -> this.getTypeFromIdentifier(param.getType()))
                .collect(Collectors.toList());

        Entry function = new FunctionEntry(formalParamTypes, returnType);
        table.insertEntry(session.intern(node.getName()), function);
    }

    @Override
    public void enterBody(FunctionDeclarationNode node) {
        List<Type> formalParamTypes = node.getParameters()
                .stream()
                .map(param -> this.getTypeFromIdentifier(param.getType()))
                .collect(Collectors.toList());
        table.enterScope();

        List<FieldDeclarationNode> parameters = node.getParameters();
        for (int i = 0; i < formalParamTypes.size(); i++) {
            Symbol name = session.intern(parameters.get(i).getName());
            Type type = formalParamTypes.get(i);
            table.insertEntry(name, new VariableEntry(type));
        }
    }

    @Override
    public void exitBody(FunctionDeclarationNode node) {
        table.exitScope();
    }

    @Override
    public void exit(FunctionDeclarationNode node) {
        FunctionEntry function = (FunctionEntry)table.queryEntry(session.intern(node.getName()));
        this.checkTypes(function.getReturnType(), node.getBody());
    }

    @Override
    public void exit(VariableDeclarationNode node) {
        Type varType;
        if (node.hasType()) {
            varType = this.getTypeFromIdentifier(node.getType());
            this.checkTypes(varType, node.getInitializer());
        } else {
            varType = session.getTypeCache().get(node.getInitializer());
            if (varType instanceof NilType) {
                this.error(node, "use of nil requires a type annotation");
                varType = ErrorType.Instance;
            }
        }

        Entry var = new VariableEntry(varType);
        table.insertEntry(session.intern(node.getName()), var);
    }

    @Override
    public void exit(IdentifierNode node) {
        Entry identType = table.queryEntry(session.intern(node.getIdentifier()));
        if (identType == null) {
            this.error(node, "unbound identifier: " + node.getIdentifier());
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }
        if (identType instanceof FunctionEntry) {
            this.error(node, "functions cannot be referenced as identifiers");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }
        session.getTypeCache().put(node, ((VariableEntry)identType).getType());
    }

    @Override
    public void exit(ArrayAccessNode node) {
        Type baseType = session.getTypeCache().get(node.getBase());
        if (!(baseType instanceof ArrayType)) {
            this.error(node.getBase(), "array base must have array type");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }

        this.checkTypes(IntegerType.Instance, node.getIndex());
        session.getTypeCache().put(node, ((ArrayType) baseType).getBaseType());
    }

    @Override
    public void exit(FieldAccessNode node) {
        Type baseType = session.getTypeCache().get(node.getBase());
        if (!(baseType instanceof RecordType)) {
            this.error(node.getBase(), "field access base must be record type");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }

        RecordType recordTy = (RecordType)baseType;
        Symbol field = session.intern(node.getFieldName());
        if (!recordTy.hasField(session.intern(node.getFieldName()))) {
            this.error(node, "record has no field of name '" + node.getFieldName() + "'");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }

        session.getTypeCache().put(node, recordTy.getFieldType(field));
    }

    @Override
    public void exit(IntegerLiteralNode node) {
        session.getTypeCache().put(node, IntegerType.Instance);
    }

    @Override
    public void exit(StringLiteralNode node) {
        session.getTypeCache().put(node, StringType.Instance);
    }

    @Override
    public void exit(SequenceExpressionNode node) {
        if (node.getSequence().isEmpty()) {
            session.getTypeCache().put(node, VoidType.Instance);
        } else {
            ExpressionNode lastExpr = node.getSequence().get(node.getSequence().size() - 1);
            session.getTypeCache().put(node, session.getTypeCache().get(lastExpr));
        }
    }

    @Override
    public void exit(NegationExpressionNode node) {
        this.checkTypes(IntegerType.Instance, node.getNegatedExpression());
        session.getTypeCache().put(node, IntegerType.Instance);
    }

    @Override
    public void exit(CallExpressionNode node) {
        Symbol name = session.intern(node.getFunctionName());
        Entry fun = table.queryEntry(session.intern(node.getFunctionName()));
        if (fun == null) {
            this.error(node, "function '" + name.getValue() + "' does not exist in current context");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }
        if (!(fun instanceof FunctionEntry)) {
            this.error(node, "identifier '" + name.getValue() + "' does not refer to a function");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }
        FunctionEntry functionEntry = (FunctionEntry)fun;

        if (node.getParameters().size() != functionEntry.getFormalParameters().size()) {
            this.error(node, "arity mismatch: expected " + functionEntry.getFormalParameters().size()
                    + " parameters, got " + node.getParameters().size());
        }

        List<ExpressionNode> actualParameters = node.getParameters();
        List<Type> formalParameters = functionEntry.getFormalParameters();
        for (int i = 0; i < actualParameters.size(); i++) {
            Type formal = formalParameters.get(i);
            this.checkTypes(formal, actualParameters.get(i));
        }

        session.getTypeCache().put(node, functionEntry.getReturnType());
    }

    @Override
    public void exit(ArrayCreationExpressionNode node) {
        Type arrayType = this.getTypeFromIdentifier(node.getTypeName());
        Type resultType = ErrorType.Instance;
        if (!arrayType.isArray()) {
            this.error(node.getTypeName(), "'" + node.getTypeName().getName() + "' does not " +
                    "specify an array type");
        } else {
            if (arrayType.isError()) {
                resultType = ErrorType.Instance;
            } else {
                resultType = ((ArrayType)arrayType).getBaseType();
            }
        }

        this.checkTypes(IntegerType.Instance, node.getLengthExpression());
        if (!resultType.isError()) {
            this.checkTypes(resultType, node.getInitializer());
        }
        session.getTypeCache().put(node, arrayType);
    }

    @Override
    public void exit(RecordCreationExpressionNode node) {
        Type recordType = this.getTypeFromIdentifier(node.getTypeName());
        if (!recordType.isRecord()) {
            this.error(node.getTypeName(), "'" + node.getTypeName().getName() + "' does not " +
                    "specify a record type");
            session.getTypeCache().put(node, ErrorType.Instance);
            return;
        }

        if (recordType.isError()) {
            return;
        }

        List<FieldCreationNode> fields = node.getFields();
        List<RecordField> actualFields = ((RecordType)recordType).getFields();
        for (int i = 0; i < fields.size(); i++) {
            if (session.intern(fields.get(i).getFieldName()) != actualFields.get(i).name) {
                this.error(fields.get(i), "field '" + fields.get(i).getFieldName() + "' does not exist on type " +
                        node.getTypeName().getName());
            }
            this.checkTypes(actualFields.get(i).type, fields.get(i).getFieldValue());
        }
        session.getTypeCache().put(node, recordType);
    }

    @Override
    public void exit(AssignmentNode node) {
        // can't assign to the loop index
        if ((node.getAssignmentTarget() instanceof IdentifierNode)) {
            IdentifierNode ident = (IdentifierNode)node.getAssignmentTarget();
            Entry e = table.queryEntry(session.intern(ident.getIdentifier()));
            if (e != null && e instanceof VariableEntry) {
                if (((VariableEntry) e).isLoopVariable()) {
                    this.error(node.getAssignmentTarget(), "cannot assign to loop variable");
                }
            }
        }
        Type lvalue = session.getTypeCache().get(node.getAssignmentTarget());
        this.checkTypes(lvalue, node.getExpression());
        session.getTypeCache().put(node, VoidType.Instance);
    }

    @Override
    public void exit(ConditionalExpressionNode node) {
        this.checkTypes(IntegerType.Instance, node.getCondition());
        if (node.hasFalseBranch()) {
            Type trueBranch = session.getTypeCache().get(node.getTrueBranch());
            this.checkTypes(trueBranch, node.getFalseBranch());
            session.getTypeCache().put(node, trueBranch);
        } else {
            this.checkTypes(VoidType.Instance, node.getTrueBranch());
            session.getTypeCache().put(node, VoidType.Instance);
        }
    }

    @Override
    public void enterBody(WhileExpressionNode node) {
        loopDepth++;
    }

    @Override
    public void exitBody(WhileExpressionNode node) {
        loopDepth--;
    }

    @Override
    public void exit(WhileExpressionNode node) {
        this.checkTypes(IntegerType.Instance, node.getCondition());
        this.checkTypes(VoidType.Instance, node.getBody());
        session.getTypeCache().put(node, VoidType.Instance);
    }

    @Override
    public void enter(ForExpressionNode node) {
        table.enterScope();
        VariableEntry loopVariable = new VariableEntry(IntegerType.Instance);
        loopVariable.setLoopVariable(true);
        table.insertEntry(session.intern(node.getIdentifier()), loopVariable);
    }

    @Override
    public void enterBody(ForExpressionNode node) {
        loopDepth++;
    }

    @Override
    public void exitBody(ForExpressionNode node) {
        loopDepth--;
    }

    @Override
    public void exit(ForExpressionNode node) {
        this.checkTypes(IntegerType.Instance, node.getBindingExpression());
        this.checkTypes(IntegerType.Instance, node.getToExpression());
        this.checkTypes(VoidType.Instance, node.getBody());
        table.exitScope();
    }

    @Override
    public void exit(BreakNode node) {
        if (!this.inLoop()) {
            this.error(node, "cannot break when not in a loop");
        }
        session.getTypeCache().put(node, VoidType.Instance);
    }

    @Override
    public void enter(LetExpressionNode node) {
        table.enterScope();
    }

    @Override
    public void exit(LetExpressionNode node) {
        if (node.getBody().isEmpty()) {
            session.getTypeCache().put(node, VoidType.Instance);
        } else {
            ExpressionNode lastExpr = node.getBody().get(node.getBody().size() - 1);
            session.getTypeCache().put(node, session.getTypeCache().get(lastExpr));
        }
    }

    @Override
    public void exit(InfixExpressionNode node) {
        switch (node.getOperator()) {
            case Plus:
            case Minus:
            case Mul:
            case Div:
            case And:
            case Or:
                this.checkTypes(IntegerType.Instance, node.getLeft());
                this.checkTypes(IntegerType.Instance, node.getRight());
                session.getTypeCache().put(node, IntegerType.Instance);
                break;
            case Eq:
            case Neq:
                Type lhs = session.getTypeCache().get(node.getLeft());
                this.checkTypes(lhs, node.getRight());
                session.getTypeCache().put(node, IntegerType.Instance);
                break;
            case LessThan:
            case Leq:
            case GreaterThan:
            case Geq:
                Type left = session.getTypeCache().get(node.getLeft());
                this.checkTypes(left, node.getRight());
                if (!left.isEquivalent(IntegerType.Instance) && !left.isEquivalent(StringType.Instance)) {
                    this.error(node, "operator requires the operators to be of type int or string");
                }
                session.getTypeCache().put(node, IntegerType.Instance);
                break;
        }
    }

    @Override
    public void enter(TypeDeclarationNode node) {
        Type newType = this.typeNodeToType(node.getType());
        table.insertType(session.intern(node.getName()), newType);
    }

    @Override
    public void exit(NilNode node) {
        session.getTypeCache().put(node, NilType.Instance);
    }

    private Type typeNodeToType(TypeNode node) {
        if (node instanceof IdentifierTypeNode) {
            return this.getTypeFromIdentifier((IdentifierTypeNode)node);
        }
        if (node instanceof ArrayTypeNode) {
            Type baseType = this.getTypeFromIdentifier(((ArrayTypeNode) node).getArrayTypeName());
            return new ArrayType(baseType);
        }
        if (node instanceof RecordTypeNode) {
            RecordTypeNode record = (RecordTypeNode)node;
            List<Symbol> fieldNames = record.getFields()
                    .stream()
                    .map(FieldDeclarationNode::getName)
                    .map(session::intern)
                    .collect(Collectors.toList());
            List<Type> fieldTypes = record.getFields()
                    .stream()
                    .map(f -> this.getTypeFromIdentifier(f.getType()))
                    .collect(Collectors.toList());
            CompilerAssert.check(fieldNames.size() == fieldTypes.size(), "names and type list have different sizes?");
            List<RecordField> fields = new ArrayList<>();
            for (int i = 0; i < fieldNames.size(); i++) {
                fields.add(new RecordField(fieldNames.get(i), fieldTypes.get(i)));
            }
            return new RecordType(fields);
        }
        CompilerAssert.panic("unreachable code");
        return null;
    }
}
