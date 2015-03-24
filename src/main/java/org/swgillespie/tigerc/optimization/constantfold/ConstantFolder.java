package org.swgillespie.tigerc.optimization.constantfold;

import org.swgillespie.tigerc.ast.InfixOperator;
import org.swgillespie.tigerc.canonicalize.IRMutator;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.trans.ir.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sean on 3/16/15.
 */
public class ConstantFolder extends IRMutator {
    private Map<IRExpression, Integer> evaluatedConstants;
    private CompilationSession session;

    public ConstantFolder(CompilationSession session) {
        this.session = session;
        this.evaluatedConstants = new HashMap<>();
    }

    @Override
    public IRExpression visitConst(IRConst node) {
        this.evaluatedConstants.put(node, node.getImmediateValue());
        return node;
    }

    @Override
    public IRExpression visitBinop(IRBinop node) {
        node.setLeft(this.visitExpression(node.getLeft()));
        node.setRight(this.visitExpression(node.getRight()));

        Integer left = this.evaluatedConstants.get(node.getLeft());
        Integer right = this.evaluatedConstants.get(node.getRight());
        IRConst newNode;
        if (left == null || right == null) {
            // if either one of these branches isn't const, we can only
            // do a few things.
            if (left == null && right != null) {
                // we can work with this.
                if (right == 0) {
                    // adding or subtracting by the additive identity is a no-op
                    if (node.getOp() == InfixOperator.Minus || node.getOp() == InfixOperator.Plus) {
                        return node.getLeft();
                    }
                    return node;
                }
                if (right == 1) {
                    // multiplying or dividing by the multiplicative identity is a no-op
                    // dividing is okay since this is the rhs of the binop
                    if (node.getOp() == InfixOperator.Mul || node.getOp() == InfixOperator.Div) {
                        return node.getLeft();
                    }
                    return node;
                }
                // otherwise there's nothing we can do.
                return node;
            } else if (left != null) {
                // same as above, except we can't fold division or subtraction
                if (left == 0) {
                    if (node.getOp() == InfixOperator.Plus) {
                        return node.getRight();
                    }
                    return node;
                }
                if (left == 1) {
                    return node.getOp() == InfixOperator.Mul ? node.getRight() : node;
                }
                return node;
            }
            // we definitely can't do anything if neither of them are const
            return node;
        }
        // if they are both const, though, we can eliminate this operation
        switch (node.getOp()) {
            case And:
                newNode = new IRConst(left & right);
                this.evaluatedConstants.put(newNode, left & right);
                return newNode;
            case Or:
                newNode = new IRConst(left | right);
                this.evaluatedConstants.put(newNode, left | right);
                return newNode;
            case Plus:
                newNode = new IRConst(left + right);
                this.evaluatedConstants.put(newNode, left + right);
                return newNode;
            case Minus:
                newNode = new IRConst(left - right);
                this.evaluatedConstants.put(newNode, left - right);
                return newNode;
            case Mul:
                newNode = new IRConst(left * right);
                this.evaluatedConstants.put(newNode, left * right);
                return newNode;
            case Div:
                if (right == 0) {
                    // please don't make my compiler divide by zero :(
                    newNode = new IRConst(0);
                    this.evaluatedConstants.put(newNode, 0);
                    return newNode;
                } else {
                    newNode = new IRConst(left / right);
                    this.evaluatedConstants.put(newNode, left / right);
                    return newNode;
                }
            case Eq:
                newNode = new IRConst(Objects.equals(left, right) ? 1 : 0);
                this.evaluatedConstants.put(newNode, Objects.equals(left, right) ? 1 : 0);
                return newNode;
            case Neq:
                newNode = new IRConst(!Objects.equals(left, right) ? 1 : 0);
                this.evaluatedConstants.put(newNode, !Objects.equals(left, right) ? 1 : 0);
                return newNode;
            case Geq:
                newNode = new IRConst(left >= right ? 1 : 0);
                this.evaluatedConstants.put(newNode, left >= right ? 1 : 0);
                return newNode;
            case GreaterThan:
                newNode = new IRConst(left > right ? 1 : 0);
                this.evaluatedConstants.put(newNode, left > right ? 1 : 0);
                return newNode;
            case Leq:
                newNode = new IRConst(left <= right ? 1 : 0);
                this.evaluatedConstants.put(newNode, left <= right ? 1 : 0);
                return newNode;
            case LessThan:
                newNode = new IRConst(left < right ? 1 : 0);
                this.evaluatedConstants.put(newNode, left < right ? 1 : 0);
                return newNode;
            default:
                return node;
        }
    }

    @Override
    public IRStatement visitConditionalJump(IRConditionalJump node) {
        node.setFirstExpr(this.visitExpression(node.getFirstExpr()));
        node.setSecondExpr(this.visitExpression(node.getSecondExpr()));
        // if we can prove that the condition of this jump is const,
        // we can turn this conditional jump into an unconditional jump.
        Integer first = this.evaluatedConstants.get(node.getFirstExpr());
        Integer second = this.evaluatedConstants.get(node.getSecondExpr());
        if (first == null || second == null) {
            return node;
        }
        switch (node.getRelationalOp()) {
            case Neq:
                if (!Objects.equals(first, second)) {
                    // if this is true, we can turn this into
                    // an unconditional jump to the true branch.
                    return new IRJump(node.getTrueTarget());
                } else {
                    return new IRJump(node.getFalseTarget());
                }
            default:
                // we don't really use relational op and we might kill it.
                return node;
        }
    }
}
