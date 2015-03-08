package org.swgillespie.tigerc.common;

import org.swgillespie.tigerc.ast.*;

/**
 * Created by sean on 3/2/15.
 */
public interface AstVisitor {
    public void enter(ArrayAccessNode node);
	public void enter(ArrayCreationExpressionNode node);
    public void enter(ArrayTypeNode node);
    public void enter(AssignmentNode node);
    public void enter(BreakNode node);
    public void enter(CallExpressionNode node);
    public void enter(ConditionalExpressionNode node);
    public void enter(DeclarationNode node);
    public void enter(ExpressionNode node);
    public void enter(FieldAccessNode node);
    public void enter(FieldCreationNode node);
    public void enter(FieldDeclarationNode node);
    public void enter(ForExpressionNode node);
    public void enterBody(ForExpressionNode node);
    public void enter(FunctionDeclarationNode node);
    public void enterBody(FunctionDeclarationNode node);
    public void enter(IdentifierNode node);
    public void enter(IdentifierTypeNode node);
    public void enter(InfixExpressionNode node);
    public void enter(IntegerLiteralNode node);
    public void enter(LetExpressionNode node);
    public void enter(LValueNode node);
    public void enter(NegationExpressionNode node);
    public void enter(NilNode node);
    public void enter(RecordCreationExpressionNode node);
    public void enter(RecordTypeNode node);
    public void enter(SequenceExpressionNode node);
    public void enter(StringLiteralNode node);
    public void enter(TypeDeclarationNode node);
    public void enter(TypeNode node);
    public void enter(VariableDeclarationNode node);
    public void enter(WhileExpressionNode node);
    public void enterBody(WhileExpressionNode node);
    public void exit(ArrayAccessNode node);
    public void exit(ArrayCreationExpressionNode node);
    public void exit(ArrayTypeNode node);
    public void exit(AssignmentNode node);
    public void exit(BreakNode node);
    public void exit(CallExpressionNode node);
    public void exit(ConditionalExpressionNode node);
    public void exit(DeclarationNode node);
    public void exit(ExpressionNode node);
    public void exit(FieldAccessNode node);
    public void exit(FieldCreationNode node);
    public void exit(FieldDeclarationNode node);
    public void exit(ForExpressionNode node);
    public void exitBody(ForExpressionNode node);
    public void exit(FunctionDeclarationNode node);
    public void exitBody(FunctionDeclarationNode node);
    public void exit(IdentifierNode node);
    public void exit(IdentifierTypeNode node);
    public void exit(InfixExpressionNode node);
    public void exit(IntegerLiteralNode node);
    public void exit(LetExpressionNode node);
    public void exit(LValueNode node);
    public void exit(NegationExpressionNode node);
    public void exit(NilNode node);
    public void exit(RecordCreationExpressionNode node);
    public void exit(RecordTypeNode node);
    public void exit(SequenceExpressionNode node);
    public void exit(StringLiteralNode node);
    public void exit(TypeDeclarationNode node);
    public void exit(TypeNode node);
    public void exit(VariableDeclarationNode node);
    public void exit(WhileExpressionNode node);
    public void exitBody(WhileExpressionNode node);
}