package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

/**
 * Created by sean on 2/28/15.
 */
public final class ArrayCreationExpressionNode extends ExpressionNode {
    private IdentifierTypeNode typeName;
    private ExpressionNode lengthExpression;
    private ExpressionNode initializer;

    public ArrayCreationExpressionNode(TextSpan span, IdentifierTypeNode typeName, ExpressionNode lengthExpression, ExpressionNode initializer) {
        super(span);
        this.typeName = typeName;
        this.lengthExpression = lengthExpression;
        this.initializer = initializer;
    }

    public IdentifierTypeNode getTypeName() {
        return typeName;
    }

    public void setTypeName(IdentifierTypeNode typeName) {
        this.typeName = typeName;
    }

    public ExpressionNode getLengthExpression() {
        return lengthExpression;
    }

    public void setLengthExpression(ExpressionNode lengthExpression) {
        this.lengthExpression = lengthExpression;
    }

    public ExpressionNode getInitializer() {
        return initializer;
    }

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "ArrayCreationExpression{" +
                "typeName='" + typeName + '\'' +
                ", lengthExpression=" + lengthExpression +
                ", initializer=" + initializer +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        typeName.accept(visitor);
        lengthExpression.accept(visitor);
        initializer.accept(visitor);
        visitor.exit(this);
    }
}
