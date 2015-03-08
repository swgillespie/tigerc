package org.swgillespie.tigerc.ast;

import org.swgillespie.tigerc.common.AstVisitor;

import java.util.List;

/**
 * Created by sean on 2/28/15.
 */
public final class SequenceExpressionNode extends ExpressionNode {
    private List<ExpressionNode> sequence;

    public SequenceExpressionNode(TextSpan span, List<ExpressionNode> sequence) {
        super(span);
        this.sequence = sequence;
    }

    public List<ExpressionNode> getSequence() {
        return sequence;
    }

    public void setSequence(List<ExpressionNode> sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean isConst() {
        for (ExpressionNode node : sequence) {
            if (!node.isConst()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "SequenceExpressionNode{" +
                "sequence=" + sequence +
                '}';
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.enter(this);
        sequence.forEach(s -> s.accept(visitor));
        visitor.exit(this);
    }
}
