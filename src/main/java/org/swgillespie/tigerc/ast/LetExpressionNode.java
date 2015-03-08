package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/28/15.
 */
public final class LetExpressionNode extends ExpressionNode {
    private List<DeclarationNode> declarations;
    private List<ExpressionNode> body;

    public LetExpressionNode(TextSpan span, List<DeclarationNode> declarations, List<ExpressionNode> body) {
        super(span);
        this.declarations = declarations;
        this.body = body;
    }

    public List<DeclarationNode> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<DeclarationNode> declarations) {
        this.declarations = declarations;
    }

    public List<ExpressionNode> getBody() {
        return body;
    }

    public void setBody(List<ExpressionNode> body) {
        this.body = body;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String toString() {
        return "LetExpressionNode{" +
                "body=" + body +
                ", declarations=" + declarations +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        declarations.forEach(d -> d.accept(visitor));
        body.forEach(e -> e.accept(visitor));
        visitor.exit(this);
    }
}
