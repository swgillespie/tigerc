package org.swgillespie.tigerc.canonicalize;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.ir.*;

import java.util.stream.Collectors;

/**
 * Created by sean on 3/12/15.
 */
public abstract class IRMutator {
    public IRExpression visitBinop(IRBinop node) {
        node.setOp(node.getOp());
        node.setLeft(this.visitExpression(node.getLeft()));
        node.setRight(this.visitExpression(node.getRight()));
        return node;
    }

    public IRExpression visitCall(IRCall node) {
        node.setFunction(this.visitExpression(node.getFunction()));
        node.setArguments(node.getArguments()
                .stream()
                .map(this::visitExpression)
                .collect(Collectors.toList()));
        return node;
    }

    public IRStatement visitConditionalJump(IRConditionalJump node) {
        node.setFirstExpr(this.visitExpression(node.getFirstExpr()));
        node.setSecondExpr(this.visitExpression(node.getSecondExpr()));
        return node;
    }

    public IRExpression visitConst(IRConst node) {
        return node;
    }

    public IRStatement visitEvalAndDiscard(IREvalAndDiscard node) {
        node.setExpr(this.visitExpression(node.getExpr()));
        return node;
    }

    public IRExpression visitExpression(IRExpression node) {
        if (node instanceof IRBinop) {
            return this.visitBinop((IRBinop)node);
        }
        if (node instanceof IRCall) {
            return this.visitCall((IRCall)node);
        }
        if (node instanceof IRConst) {
            return this.visitConst((IRConst)node);
        }
        if (node instanceof IRExpressionSequence) {
            return this.visitExpressionSequence((IRExpressionSequence)node);
        }
        if (node instanceof IRMem) {
            return this.visitMem((IRMem)node);
        }
        if (node instanceof IRName) {
            return this.visitName((IRName)node);
        }
        if (node instanceof IRTemp) {
            return this.visitTemp((IRTemp)node);
        }
        CompilerAssert.panic("unreachable code");
        return null;
    }

    public IRExpression visitExpressionSequence(IRExpressionSequence node) {
        node.setStmt(this.visitStatement(node.getStmt()));
        node.setResult(this.visitExpression(node.getResult()));
        return node;
    }

    public IRStatement visitJump(IRJump node) {
        node.setTarget(this.visitExpression(node.getTarget()));
        return node;
    }

    public IRStatement visitLabel(IRLabel node) {
        return node;
    }

    public IRExpression visitMem(IRMem node) {
        node.setAddress(this.visitExpression(node.getAddress()));
        return node;
    }

    public IRStatement visitMoveMem(IRMoveMem node) {
        node.setAddressExpression(this.visitExpression(node.getAddressExpression()));
        node.setValue(this.visitExpression(node.getValue()));
        return node;
    }

    public IRStatement visitMoveTemp(IRMoveTemp node) {
        node.setValue(this.visitExpression(node.getValue()));
        return node;
    }

    public IRExpression visitName(IRName node) {
        return node;
    }

    public IRStatement visitSeq(IRSeq node) {
        node.setFirst(this.visitStatement(node.getFirst()));
        node.setSecond(this.visitStatement(node.getSecond()));
        return node;
    }

    public IRStatement visitStatement(IRStatement node) {
        if (node instanceof IRConditionalJump) {
            return this.visitConditionalJump((IRConditionalJump)node);
        }
        if (node instanceof IREvalAndDiscard) {
            return this.visitEvalAndDiscard((IREvalAndDiscard)node);
        }
        if (node instanceof IRJump) {
            return this.visitJump((IRJump)node);
        }
        if (node instanceof IRLabel) {
            return this.visitLabel((IRLabel)node);
        }
        if (node instanceof IRMoveMem) {
            return this.visitMoveMem((IRMoveMem)node);
        }
        if (node instanceof IRMoveTemp) {
            return this.visitMoveTemp((IRMoveTemp)node);
        }
        if (node instanceof IRSeq) {
            return this.visitSeq((IRSeq)node);
        }
        CompilerAssert.panic("unreachable code");
        return null;
    }

    public IRExpression visitTemp(IRTemp node) {
        return node;
    }
}
