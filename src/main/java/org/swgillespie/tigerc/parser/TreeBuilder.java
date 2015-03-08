package org.swgillespie.tigerc.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.swgillespie.tigerc.common.InternalCompilerException;
import org.swgillespie.tigerc.ast.*;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sean on 2/28/15.
 */
@SuppressWarnings("unchecked")
class TreeBuilder extends org.swgillespie.tigerc.parser.TigerBaseVisitor {
    private static TextSpan toTextSpan(ParserRuleContext ctx) {
        TextPosition start = new TextPosition(ctx.getStart().getLine(), ctx.getStart().getStartIndex());
        TextPosition stop = new TextPosition(ctx.getStop().getLine(), ctx.getStop().getStopIndex());
        return new TextSpan(start, stop);
    }

    public AstNode buildAst(ParseTree tree) {
        return (AstNode)this.visit(tree);
    }

    @Override
    public Object visitArray_create_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Array_create_expContext ctx) {
        String name = ctx.getChild(0).getText();
        ExpressionNode base = (ExpressionNode)this.visit(ctx.getChild(2));
        ExpressionNode initializer = (ExpressionNode)this.visit(ctx.getChild(5));
        IdentifierTypeNode type = new IdentifierTypeNode(toTextSpan(ctx), name);
        return new ArrayCreationExpressionNode(toTextSpan(ctx), type, base, initializer);
    }

    @Override
    public Object visitAssign_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Assign_expContext ctx) {
        LValueNode lvalue = (LValueNode)this.visit(ctx.getChild(0));
        ExpressionNode target = (ExpressionNode)this.visit(ctx.getChild(2));
        return new AssignmentNode(toTextSpan(ctx), lvalue, target);
    }

    @Override
    public Object visitBreak_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Break_expContext ctx) {
        return new BreakNode(toTextSpan(ctx));
    }

    @Override
    public Object visitCall_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Call_expContext ctx) {
        String callIdent = ctx.getChild(0).getText();
        List<ExpressionNode> args = (List<ExpressionNode>)this.visit(ctx.getChild(2));
        return new CallExpressionNode(toTextSpan(ctx), callIdent, args);
    }

    @Override
    public Object visitComma_sep_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Comma_sep_expContext ctx) {
        if (ctx.getChildCount() == 0) {
            return new ArrayList<ExpressionNode>();
        }
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitComma_sep_field_create(@NotNull org.swgillespie.tigerc.parser.TigerParser.Comma_sep_field_createContext ctx) {
        if (ctx.getChildCount() == 0) {
            return new ArrayList<FieldCreationNode>();
        }
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitDeclarations(@NotNull org.swgillespie.tigerc.parser.TigerParser.DeclarationsContext ctx) {
        ArrayList<DeclarationNode> declarations = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            declarations.add((DeclarationNode)this.visit(ctx.getChild(i)));
        }
        return declarations;
    }

    @Override
    public Object visitField_create(@NotNull org.swgillespie.tigerc.parser.TigerParser.Field_createContext ctx) {
        String name = ctx.getChild(0).getText();
        ExpressionNode expr = (ExpressionNode)this.visit(ctx.getChild(2));
        return new FieldCreationNode(toTextSpan(ctx), name, expr);
    }

    @Override
    public Object visitField_dec(@NotNull org.swgillespie.tigerc.parser.TigerParser.Field_decContext ctx) {
        String name = ctx.getChild(0).getText();
        String ty = ctx.getChild(2).getText();
        IdentifierTypeNode type = new IdentifierTypeNode(toTextSpan(ctx), ty);
        return new FieldDeclarationNode(toTextSpan(ctx), name, type);
    }

    @Override
    public Object visitField_dec_list(@NotNull org.swgillespie.tigerc.parser.TigerParser.Field_dec_listContext ctx) {
        if (ctx.getChildCount() == 0) {
            return new ArrayList<FieldDeclarationNode>();
        }
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitFor_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.For_expContext ctx) {
        String binding = ctx.getChild(1).getText();
        ExpressionNode initialValue = (ExpressionNode)this.visit(ctx.getChild(3));
        ExpressionNode toValue = (ExpressionNode)this.visit(ctx.getChild(5));
        ExpressionNode body = (ExpressionNode)this.visit(ctx.getChild(7));
        return new ForExpressionNode(toTextSpan(ctx), binding, initialValue, toValue, body);
    }

    @Override
    public Object visitFunction_dec(@NotNull org.swgillespie.tigerc.parser.TigerParser.Function_decContext ctx) {
        String name = ctx.getChild(1).getText();
        List<FieldDeclarationNode> parameters = (List<FieldDeclarationNode>)this.visit(ctx.getChild(3));
        ExpressionNode body = (ExpressionNode)this.visit(ctx.getChild(6));
        return new FunctionDeclarationNode(toTextSpan(ctx), name, parameters, null, body);
    }

    @Override
    public Object visitFunction_dec_with_type(@NotNull org.swgillespie.tigerc.parser.TigerParser.Function_dec_with_typeContext ctx) {
        String name = ctx.getChild(1).getText();
        List<FieldDeclarationNode> parameters = (List<FieldDeclarationNode>)this.visit(ctx.getChild(3));
        String returnType = ctx.getChild(6).getText();
        IdentifierTypeNode type = new IdentifierTypeNode(toTextSpan(ctx), returnType);
        ExpressionNode body = (ExpressionNode)this.visit(ctx.getChild(8));
        return new FunctionDeclarationNode(toTextSpan(ctx), name, parameters, type, body);
    }

    @Override
    public Object visitIf_then_else_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.If_then_else_expContext ctx) {
        ExpressionNode cond = (ExpressionNode)this.visit(ctx.getChild(1));
        ExpressionNode trueBranch = (ExpressionNode)this.visit(ctx.getChild(3));
        ExpressionNode falseBranch = (ExpressionNode)this.visit(ctx.getChild(5));
        return new ConditionalExpressionNode(toTextSpan(ctx), cond, trueBranch, falseBranch);
    }

    @Override
    public Object visitIf_then_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.If_then_expContext ctx) {
        ExpressionNode cond = (ExpressionNode)this.visit(ctx.getChild(1));
        ExpressionNode trueBranch = (ExpressionNode)this.visit(ctx.getChild(3));
        return new ConditionalExpressionNode(toTextSpan(ctx), cond, trueBranch, null);
    }

    @Override
    public Object visitInfix_op(@NotNull org.swgillespie.tigerc.parser.TigerParser.Infix_opContext ctx) {
        switch (ctx.getChild(0).getText()) {
            case "*":
                return InfixOperator.Mul;
            case "/":
                return InfixOperator.Div;
            case "+":
                return InfixOperator.Plus;
            case "-":
                return InfixOperator.Minus;
            case "=":
                return InfixOperator.Eq;
            case "<>":
                return InfixOperator.Neq;
            case ">":
                return InfixOperator.GreaterThan;
            case "<":
                return InfixOperator.LessThan;
            case ">=":
                return InfixOperator.Geq;
            case "<=":
                return InfixOperator.Leq;
            case "&":
                return InfixOperator.And;
            case "|":
                return InfixOperator.Or;
            default:
                throw new InternalCompilerException("unknown infix op? " + ctx.getChild(0).getText());
        }
    }

    @Override
    public Object visitInfix_op_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Infix_op_expContext ctx) {
        ExpressionNode left = (ExpressionNode)this.visit(ctx.getChild(0));
        InfixOperator op = (InfixOperator)this.visit(ctx.getChild(1));
        ExpressionNode right = (ExpressionNode)this.visit(ctx.getChild(2));
        return new InfixExpressionNode(toTextSpan(ctx), op, left, right);
    }

    @Override
    public Object visitInteger_literal_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Integer_literal_expContext ctx) {
        return new IntegerLiteralNode(toTextSpan(ctx), Integer.valueOf(ctx.getText()));
    }

    @Override
    public Object visitL_value_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.L_value_expContext ctx) {
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitLet_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Let_expContext ctx) {
        List<DeclarationNode> declaration = (List<DeclarationNode>)this.visit(ctx.getChild(1));
        List<ExpressionNode> body = (List<ExpressionNode>)this.visit(ctx.getChild(3));
        return new LetExpressionNode(toTextSpan(ctx), declaration, body);
    }

    @Override
    public Object visitLvalue_field_access(@NotNull org.swgillespie.tigerc.parser.TigerParser.Lvalue_field_accessContext ctx) {
        LValueNode base = (LValueNode)this.visit(ctx.getChild(0));
        String ident = ctx.getChild(2).getText();
        return new FieldAccessNode(toTextSpan(ctx), base, ident);
    }

    @Override
    public Object visitLvalue_identifier(@NotNull org.swgillespie.tigerc.parser.TigerParser.Lvalue_identifierContext ctx) {
        return new IdentifierNode(toTextSpan(ctx), ctx.getText());
    }

    @Override
    public Object visitLvalue_index(@NotNull org.swgillespie.tigerc.parser.TigerParser.Lvalue_indexContext ctx) {
        LValueNode base = (LValueNode)this.visit(ctx.getChild(0));
        ExpressionNode index = (ExpressionNode)this.visit(ctx.getChild(2));
        return new ArrayAccessNode(toTextSpan(ctx), base, index);
    }

    @Override
    public Object visitNegation_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Negation_expContext ctx) {
        ExpressionNode expr = (ExpressionNode)this.visit(ctx.getChild(1));
        return new NegationExpressionNode(toTextSpan(ctx), expr);
    }

    @Override
    public Object visitNil_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Nil_expContext ctx) {
        return new NilNode(toTextSpan(ctx));
    }

    @Override
    public Object visitNonempty_comma_sep_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Nonempty_comma_sep_expContext ctx) {
        ArrayList<ExpressionNode> exprs = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i += 2) {
            exprs.add((ExpressionNode)this.visit(ctx.getChild(i)));
        }
        return exprs;
    }

    @Override
    public Object visitNonempty_comma_sep_field_create(@NotNull org.swgillespie.tigerc.parser.TigerParser.Nonempty_comma_sep_field_createContext ctx) {
        ArrayList<FieldCreationNode> fields = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i += 2) {
            fields.add((FieldCreationNode)this.visit(ctx.getChild(i)));
        }
        return fields;
    }

    @Override
    public Object visitNonempty_field_dec_list(@NotNull org.swgillespie.tigerc.parser.TigerParser.Nonempty_field_dec_listContext ctx) {
        ArrayList<FieldDeclarationNode> fields = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i += 2) {
            fields.add((FieldDeclarationNode)this.visit(ctx.getChild(i)));
        }
        return fields;
    }

    @Override
    public Object visitNonempty_semi_sep_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Nonempty_semi_sep_expContext ctx) {
        ArrayList<ExpressionNode> exprs = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i += 2) {
            exprs.add((ExpressionNode)this.visit(ctx.getChild(i)));
        }
        return exprs;
    }

    @Override
    public Object visitParen_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Paren_expContext ctx) {
        return new SequenceExpressionNode(toTextSpan(ctx), (List<ExpressionNode>)this.visit(ctx.getChild(1)));
    }

    @Override
    public Object visitProgram(@NotNull org.swgillespie.tigerc.parser.TigerParser.ProgramContext ctx) {
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitRec_create_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Rec_create_expContext ctx) {
        String typeName = ctx.getChild(0).getText();
        IdentifierTypeNode type = new IdentifierTypeNode(toTextSpan(ctx), typeName);
        List<FieldCreationNode> fields = (List<FieldCreationNode>)this.visit(ctx.getChild(2));
        return new RecordCreationExpressionNode(toTextSpan(ctx), type, fields);
    }

    @Override
    public Object visitSemi_sep_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.Semi_sep_expContext ctx) {
        if (ctx.getChildCount() == 0) {
            return new ArrayList<>();
        }
        return this.visit(ctx.getChild(0));
    }

    @Override
    public Object visitString_literal_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.String_literal_expContext ctx) {
        String value = ctx.getText();
        return new StringLiteralNode(toTextSpan(ctx), value.substring(1, value.length() - 1));
    }

    @Override
    public Object visitTy_array(@NotNull org.swgillespie.tigerc.parser.TigerParser.Ty_arrayContext ctx) {
        String name = ctx.getChild(2).getText();
        IdentifierTypeNode type = new IdentifierTypeNode(toTextSpan(ctx), name);
        return new ArrayTypeNode(toTextSpan(ctx), type);
    }

    @Override
    public Object visitTy_fundamental(@NotNull org.swgillespie.tigerc.parser.TigerParser.Ty_fundamentalContext ctx) {
        return new IdentifierTypeNode(toTextSpan(ctx), ctx.getText());
    }

    @Override
    public Object visitTy_record(@NotNull org.swgillespie.tigerc.parser.TigerParser.Ty_recordContext ctx) {
        List<FieldDeclarationNode> fields = (List<FieldDeclarationNode>)this.visit(ctx.getChild(1));
        return new RecordTypeNode(toTextSpan(ctx), fields);
    }

    @Override
    public Object visitType_dec(@NotNull org.swgillespie.tigerc.parser.TigerParser.Type_decContext ctx) {
        String name = ctx.getChild(1).getText();
        TypeNode type = (TypeNode)this.visit(ctx.getChild(3));
        return new TypeDeclarationNode(toTextSpan(ctx), name, type);
    }

    @Override
    public Object visitVar_dec(@NotNull org.swgillespie.tigerc.parser.TigerParser.Var_decContext ctx) {
        String name = ctx.getChild(1).getText();
        ExpressionNode expr = (ExpressionNode)this.visit(ctx.getChild(3));
        return new VariableDeclarationNode(toTextSpan(ctx), name, expr, null);
    }

    @Override
    public Object visitVar_dec_with_type(@NotNull org.swgillespie.tigerc.parser.TigerParser.Var_dec_with_typeContext ctx) {
        String name = ctx.getChild(1).getText();
        String type = ctx.getChild(3).getText();
        IdentifierTypeNode ty = new IdentifierTypeNode(toTextSpan(ctx), type);
        ExpressionNode expr = (ExpressionNode)this.visit(ctx.getChild(5));
        return new VariableDeclarationNode(toTextSpan(ctx), name, expr, ty);
    }

    @Override
    public Object visitWhile_exp(@NotNull org.swgillespie.tigerc.parser.TigerParser.While_expContext ctx) {
        ExpressionNode condition = (ExpressionNode)this.visit(ctx.getChild(1));
        ExpressionNode body = (ExpressionNode)this.visit(ctx.getChild(3));
        return new WhileExpressionNode(toTextSpan(ctx), condition, body);
    }
}
