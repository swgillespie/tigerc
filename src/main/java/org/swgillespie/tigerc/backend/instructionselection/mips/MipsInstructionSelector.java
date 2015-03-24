package org.swgillespie.tigerc.backend.instructionselection.mips;

import org.swgillespie.tigerc.ast.InfixOperator;
import org.swgillespie.tigerc.backend.instructionselection.*;
import org.swgillespie.tigerc.common.CompilationSession;
import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.TempLabel;
import org.swgillespie.tigerc.trans.TempRegister;
import org.swgillespie.tigerc.trans.ir.*;
import org.swgillespie.tigerc.trans.mips.MipsTempFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/19/15.
 */
public class MipsInstructionSelector extends InstructionSelector {
    /*
     * basic instruction selection patterns for MIPS32:
     * CONST 0 -> $zero
     *
     * -- Arithmetic operators
     *   "result" should contain the arithmetic result of the operation.
     * BINOP(+, e1, e2) -> add result, e1, e2
     * BINOP(-, e1, e2) -> sub result, e1, e2
     * BINOP(*, e1, e2) -> mul e1, e2;
     *                     mfhi result
     * BINOP(/, e1, e2) -> div e1, e2;
     *                     mflo result
     * BINOP(+, CONST i, e2)
     * BINOP(+, e1, CONST i) -> addi result, e1, i
     *
     * -- Comparison operators
     *   "result" should contain 0 if the comparison is false, and not-zero
     *   if the comparison is true.
     * BINOP(=, e1, e2) -> subu result, e1, e2;
     *                     sltu result, $zero, result
     *                     xori result, result, 1
     * BINOP(<>, e1, e2) -> subu result, e1, e2
     * BINOP(>, e1, e2) -> slt result, e2, e1
     * BINOP(>, e1, CONST i) -> slti result, e1, i
     * BINOP(<, e1, e2) -> slt result, e1, e2
     * BINOP(<, CONST i, e1) -> slt result, e1, i
     * BINOP(>=, e1, e2) -> BINOP(>, e1, e2) | BINOP(=, e1, e2)
     * BINOP(<=, e1, e2) -> BINOP(<, e1, e2) | BINOP(=, e1, e2)
     *
     * -- Logical operators
     *   "result" should contain 0 if the logical condition is false, and not-zero
     *   if the logical condition is true.
     * BINOP(&, e1, e2) -> and result, e1, e2
     * BINOP(&, CONST i, e1)
     * BINOP(&, e1, CONST i) -> andi result, e1, i
     * BINOP(|, e1, e2) -> or result, e1, e2
     * BINOP(|, CONST i, e1)
     * BINOP(|, e1, CONST i) -> ori result, e1, i
     *
     * -- Memory store/load operations
     * MEM(e1), not in LHS of MoveMem -> lw result, 0(e1)
     * TEMP t -> t
     * CALL(f, args) -> (complicated)
     *
     * -- Branches and jumps
     * JUMP(target) -> j target
     * CJUMP(e1, e2, op, lt, lf) -> cond = (condition codegen)
     *                              bne cond, $zero, lt
     *
     * -- Moves
     * MOVE(TEMP t, e) -> addi t, e1, $zero (may be eliminated by register allocation,
     *                                       if allocator can assign e1 and t the same register)
     * MOVE(MEM(e1), e2) -> sw e2, 0(e1)
     * MOVE(MEM(BINOP(+, e1, CONST i)), e1) -> sw e2, i(e1)
     *
     * -- Label
     * LABEL(n) -> n:
     */

    private CompilationSession session;
    private MipsTempFactory tempFactory;
    private List<Instruction> instructions;

    public MipsInstructionSelector(CompilationSession session) {
        this.session = session;
        this.instructions = new ArrayList<>();
        this.tempFactory = (MipsTempFactory)session.getTempFactory();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void clearInstructions() {
        this.instructions = new ArrayList<>();
    }

    private void emit(Instruction instr) {
        this.instructions.add(instr);
    }

    private void emitMove(String asm, List<TempRegister> destRegs, List<TempRegister> srcRegs) {
        this.emit(new MoveInstruction(asm, destRegs, srcRegs));
    }

    private void emitOther(String asm,
                           List<TempRegister> destRegs,
                           List<TempRegister> srcRegs,
                           List<TempLabel> jumpTargets) {
        this.emit(new OtherInstruction(asm, destRegs, srcRegs, new AssemblyTargets(jumpTargets)));
    }

    private String constToString(IRConst value) {
        return Integer.toString(value.getImmediateValue());
    }

    @Override
    public void munchStatement(IRStatement stmt) {
        // this is going to suck because of Java's lack of unions and pattern matching.
        // excuse the horrendous code.
        if (stmt instanceof IRLabel) {
            TempLabel label = ((IRLabel) stmt).getLabel();
            this.emit(new LabelInstruction(label.getName() + ":\n", label));
            return;
        }
        if (stmt instanceof IRMoveTemp) {
            munchMoveTemp((IRMoveTemp) stmt);
            return;
        }
        if (stmt instanceof IRMoveMem) {
            munchMoveMem((IRMoveMem) stmt);
            return;
        }
        if (stmt instanceof IREvalAndDiscard) {
            CompilerAssert.check(((IREvalAndDiscard) stmt).getExpr() instanceof IRCall, "EXP should only be around CALL");
            // we have to do the same thing that we do for function calls as we do in muchMoveTemp,
            // except we don't have to move the return value from $v0 to a temp register.
            IRCall call = (IRCall)((IREvalAndDiscard) stmt).getExpr();
            CompilerAssert.check(call.getFunction() instanceof IRName, "can't call function pointers yet");
            TempLabel function = ((IRName)call.getFunction()).getName();
            List<TempRegister> sourceRegs = this.munchArgs(call);
            this.emitOther("jal " + function.getName(),
                    tempFactory.callerSaveRegisters(),
                    sourceRegs,
                    Arrays.asList());
            return;
        }
        if (stmt instanceof IRJump) {
            CompilerAssert.check(((IRJump) stmt).getTarget() instanceof IRName, "can't jump to a non-label");
            TempLabel label = ((IRName)((IRJump) stmt).getTarget()).getName();
            this.emitOther("j " + label.getName(),
                    Arrays.asList(),
                    Arrays.asList(),
                    Arrays.asList(label));
            return;
        }
        if (stmt instanceof IRConditionalJump) {
            IRConditionalJump cond = (IRConditionalJump)stmt;
            TempRegister first = this.munchExpression(cond.getFirstExpr());
            TempRegister second = this.munchExpression(cond.getSecondExpr());
            TempRegister result = tempFactory.newTemp();
            switch (cond.getRelationalOp()) {
                case Eq:
                    result = this.munchEq(result, first, second);
                    break;
                case Neq:
                    result = this.munchNeq(result, first, second);
                    break;
                default:
                    CompilerAssert.panic("unreachable code, only eq and neq allowed in cond");
                    return;
            }
            // we've guaranteed in earlier passes that the false label immediately follows
            // this statement. We only need to jump if the condition is true (i.e. result != 0)
            this.emitOther("bne `s0, `s1, " + cond.getTrueTarget().getName(),
                    Arrays.asList(),
                    Arrays.asList(result, tempFactory.Zero),
                    Arrays.asList(cond.getTrueTarget(), cond.getFalseTarget()));
        }
    }

    private void munchMoveMem(IRMoveMem stmt) {
        // this is where we can do most of our work.
        // MIPS offers us a lot of shortcuts if we can supply it
        // with immediate values, so we try to do so as much as we can.
        IRMem target = (IRMem) stmt.getAddressExpression();
        TempRegister valueReg = this.munchExpression(stmt.getValue());
        if (target.getAddress() instanceof IRBinop && ((IRBinop) target.getAddress()).getOp() == InfixOperator.Plus) {
            // extract immediate values, if we can
            IRBinop binop = (IRBinop)target.getAddress();
            if (binop.getRight() instanceof IRConst) {
                // if the RHS is const, emit an immediate load.
                String constValue = this.constToString((IRConst)binop.getRight());
                TempRegister lhsReg = this.munchExpression(binop.getLeft());
                this.emitMove("sw `s0, " + constValue + "(`s1)",
                        Arrays.asList(),
                        Arrays.asList(valueReg, lhsReg));
                return;
            }
            if (binop.getLeft() instanceof IRConst) {
                // we can do the same thing with the LHS
                String constValue = this.constToString((IRConst)binop.getLeft());
                TempRegister rhsReg = this.munchExpression(binop.getRight());
                this.emitMove("sw `s0, " + constValue + "(`s1)",
                        Arrays.asList(),
                        Arrays.asList(valueReg, rhsReg));
                return;
            }
            // MIPS only provides immediate moves with addition, so if it's any other kind of binop
            // there's nothing we can do. Keep going
        }
        TempRegister addressReg = this.munchExpression(stmt.getAddressExpression());
        this.emitMove("sw `s0, 0(`s1)",
                Arrays.asList(),
                Arrays.asList(valueReg, addressReg));
    }

    private void munchMoveTemp(IRMoveTemp stmt) {
        IRTemp target = stmt.getDestination();
        IRExpression exp = stmt.getValue();
        TempRegister resultReg;
        if (exp instanceof IRCall) {
            // function calls require us to do several things:
            // 1) move all parameters into the correct parameter-passing
            //    registers (or on the stack, if we're doing that)
            // 2) assign registers to all of the parameter expressions,
            // 3) emit jump and link (jal) to jump to the target function
            // 4) mark all caller-saved registers as live for the register allocator
            // 5) move the return value from $v0 to the correct temp register
            IRCall call = (IRCall)exp;
            CompilerAssert.check(call.getFunction() instanceof IRName, "can't call function pointers yet");
            TempLabel function = ((IRName)call.getFunction()).getName();
            // if this is a function call, we're going to have to do a little extra work.
            List<TempRegister> sourceRegs = this.munchArgs((IRCall) exp);
            this.emitOther("jal " + function.getName(),
                    tempFactory.callerSaveRegisters(),
                    sourceRegs,
                    Arrays.asList());
            // return value is in register v0
            resultReg = tempFactory.V0;
        } else {
            resultReg = this.munchExpression(exp);
        }
        this.emitMove("move `d0, `s0",
                Arrays.asList(target.getTemp()),
                Arrays.asList(resultReg));
    }

    private List<TempRegister> munchArgs(IRCall call) {
        CompilerAssert.check(call.getArguments().size() <= 4, "can't pass more than 4 arguments yet");
        List<TempRegister> arguments = call.getArguments()
                .stream()
                .map(this::munchExpression)
                .collect(Collectors.toList());
        switch (arguments.size()) {
            case 4:
                this.emitOther("move `d0, `s0",
                        Arrays.asList(tempFactory.A3),
                        Arrays.asList(arguments.get(3)),
                        Arrays.asList());
            case 3:
                this.emitOther("move `d0, `s0",
                        Arrays.asList(tempFactory.A2),
                        Arrays.asList(arguments.get(2)),
                        Arrays.asList());
            case 2:
                this.emitOther("move `d0, `s0",
                        Arrays.asList(tempFactory.A1),
                        Arrays.asList(arguments.get(1)),
                        Arrays.asList());
            case 1:
                this.emitOther("move `d0, `s0",
                        Arrays.asList(tempFactory.A0),
                        Arrays.asList(arguments.get(0)),
                        Arrays.asList());
        }
        return arguments;
    }

    private TempRegister munchExpression(IRExpression expr) {
        // i wish i had pattern matching! :(
        if (expr instanceof IRConst) {
            int value = ((IRConst) expr).getImmediateValue();
            if (value == 0) {
                // mips provides a helpful $zero register that is always zero.
                return tempFactory.Zero;
            }
            TempRegister reg = tempFactory.newTemp();
            String str = this.constToString((IRConst)expr);
            this.emitOther("li `d0, " + str,
                    Arrays.asList(reg),
                    Arrays.asList(),
                    Arrays.asList());
            return reg;
        }
        if (expr instanceof IRTemp) {
            return ((IRTemp) expr).getTemp();
        }
        if (expr instanceof IRBinop) {
            return munchBinop((IRBinop) expr);
        }
        if (expr instanceof IRMem) {
            return munchMem((IRMem) expr);
        }
        CompilerAssert.panic("unreachable code");
        return null;
    }

    private TempRegister munchMem(IRMem expr) {
        TempRegister result = tempFactory.newTemp();
        if (expr.getAddress() instanceof IRBinop) {
            // we can take advantage of immediate instructions if
            // this binop is addition and one of the operands is const
            IRBinop binop = (IRBinop) expr.getAddress();
            if (binop.getLeft() instanceof IRConst) {
                String value = this.constToString((IRConst)binop.getLeft());
                TempRegister addr = this.munchExpression(binop.getRight());
                this.emitOther("lw `d0, " + value + "(`s0)",
                        Arrays.asList(result),
                        Arrays.asList(addr),
                        Arrays.asList());
                return result;
            }
            if (binop.getRight() instanceof IRConst) {
                String value = this.constToString((IRConst)binop.getRight());
                TempRegister addr = this.munchExpression(binop.getLeft());
                this.emitOther("lw `d0, " + value + "(`s0)",
                        Arrays.asList(result),
                        Arrays.asList(addr),
                        Arrays.asList());
                return result;
            }
            // otherwise, we're going to have to evaluate it like any other expression.
        }
        TempRegister addr = this.munchExpression(expr.getAddress());
        this.emitOther("lw `d0, 0(`s0)",
                Arrays.asList(result),
                Arrays.asList(addr),
                Arrays.asList());
        return result;
    }

    private TempRegister munchBinop(IRBinop binop) {
        // lots of work to do here.
        if (binop.getOp() == InfixOperator.Plus) {
            // add is the only MIPS opcode that has an immediate
            // version if either the LHS or RHS are const.
            return this.munchPlus(binop);
        }
        TempRegister target = tempFactory.newTemp();
        switch (binop.getOp()) {
            case Minus: {
                TempRegister rhs = this.munchExpression(binop.getRight());
                TempRegister lhs = this.munchExpression(binop.getLeft());
                this.emitOther("sub `d0, `s0, s1",
                        Arrays.asList(target),
                        Arrays.asList(rhs, lhs),
                        Arrays.asList());
                return target;
            }
            case Mul: {
                TempRegister rhs = this.munchExpression(binop.getRight());
                TempRegister lhs = this.munchExpression(binop.getLeft());
                this.emitOther("mul `d0, `s0, `s1",
                        Arrays.asList(target),
                        Arrays.asList(rhs, lhs),
                        Arrays.asList());
                return target;
            }
            case Div: {
                TempRegister rhs = this.munchExpression(binop.getRight());
                TempRegister lhs = this.munchExpression(binop.getLeft());
                this.emitOther("div `d0, `s0, `s1",
                        Arrays.asList(target),
                        Arrays.asList(rhs, lhs),
                        Arrays.asList());
                return target;
            }
            case LessThan:
            case Eq:
            case Neq:
                return this.munchComparisonOp(binop);
            case And:
            case Or:
                return this.munchLogicalOp(binop);
            case Leq:
            case Geq:
            case GreaterThan:
                CompilerAssert.panic("greater than, geq, or leq should have been lowered by earlier passes");
                return null;
        }
        CompilerAssert.panic("unreachable code");
        return null;
    }

    private TempRegister munchPlus(IRBinop binop) {
        // our constant folding pass guarantess either one of LHS
        // or RHS could be const, but not both.
        TempRegister result = tempFactory.newTemp();
        if (binop.getLeft() instanceof IRConst) {
            String val = this.constToString((IRConst)binop.getLeft());
            TempRegister rhsReg = this.munchExpression(binop.getRight());
            this.emitOther("addi `d0, `s0, " + val,
                    Arrays.asList(result),
                    Arrays.asList(rhsReg),
                    Arrays.asList());
            return result;
        }
        if (binop.getRight() instanceof IRConst) {
            String val = this.constToString((IRConst)binop.getRight());
            TempRegister lhsReg = this.munchExpression(binop.getLeft());
            this.emitOther("addi `d0, `s0, " + val,
                    Arrays.asList(result),
                    Arrays.asList(lhsReg),
                    Arrays.asList());
            return result;
        }
        // otherwise, neither of them are const.
        TempRegister rhsReg = this.munchExpression(binop.getLeft());
        TempRegister lhsReg = this.munchExpression(binop.getRight());
        this.emitOther("add `d0, `s0, `s1",
                Arrays.asList(result),
                Arrays.asList(rhsReg, lhsReg),
                Arrays.asList());
        return result;
    }

    private TempRegister munchComparisonOp(IRBinop binop) {
        TempRegister result = tempFactory.newTemp();
        switch (binop.getOp()) {
            case LessThan: {
                if (binop.getRight() instanceof IRConst) {
                    TempRegister lhs = this.munchExpression(binop.getLeft());
                    String value = this.constToString((IRConst) binop.getRight());
                    this.emitOther("slti `d0, `s0, " + value,
                            Arrays.asList(result),
                            Arrays.asList(lhs),
                            Arrays.asList());
                    return result;
                }
                TempRegister lhs = this.munchExpression(binop.getLeft());
                TempRegister rhs = this.munchExpression(binop.getRight());
                this.emitOther("slt `d0, `s0, `s1",
                        Arrays.asList(result),
                        Arrays.asList(lhs, rhs),
                        Arrays.asList());
                return result;
            }
            case Eq: {
                TempRegister lhs = this.munchExpression(binop.getLeft());
                TempRegister rhs = this.munchExpression(binop.getRight());
                return munchEq(result, lhs, rhs);
            }
            case Neq: {
                TempRegister lhs = this.munchExpression(binop.getLeft());
                TempRegister rhs = this.munchExpression(binop.getRight());
                return munchNeq(result, lhs, rhs);
            }
            default:
                CompilerAssert.panic("unreachable code");
                return null;
        }
    }

    private TempRegister munchNeq(TempRegister result, TempRegister lhs, TempRegister rhs) {
        this.emitOther("subu `d0, `s0, `s1",
                Arrays.asList(result),
                Arrays.asList(lhs, rhs),
                Arrays.asList());
        return result;
    }

    private TempRegister munchEq(TempRegister result, TempRegister lhs, TempRegister rhs) {
        this.emitOther("subu `d0, `s0, `s1",
                Arrays.asList(result),
                Arrays.asList(lhs, rhs),
                Arrays.asList());
        this.emitOther("sltu `d0, `s0, `s1",
                Arrays.asList(result),
                Arrays.asList(tempFactory.Zero, result),
                Arrays.asList());
        this.emitOther("xori `d0, `s0, 1",
                Arrays.asList(result),
                Arrays.asList(result),
                Arrays.asList());
        return result;
    }

    private TempRegister munchLogicalOp(IRBinop binop) {
        TempRegister result = tempFactory.newTemp();
        if (binop.getLeft() instanceof IRConst) {
            TempRegister rhs = this.munchExpression(binop.getRight());
            String value = this.constToString((IRConst)binop.getLeft());
            if (binop.getOp() == InfixOperator.And) {
                this.emitOther("andi `d0, `s0, " + value,
                        Arrays.asList(result),
                        Arrays.asList(rhs),
                        Arrays.asList());
            } else {
                this.emitOther("ori `d0, `s0, " + value,
                        Arrays.asList(result),
                        Arrays.asList(rhs),
                        Arrays.asList());
            }
            return result;
        }
        if (binop.getRight() instanceof IRConst) {
            TempRegister lhs = this.munchExpression(binop.getLeft());
            String value = this.constToString((IRConst)binop.getRight());
            if (binop.getOp() == InfixOperator.And) {
                this.emitOther("andi `d0, `s0, " + value,
                        Arrays.asList(result),
                        Arrays.asList(lhs),
                        Arrays.asList());
            } else {
                this.emitOther("ori `d0, `s0, " + value,
                        Arrays.asList(result),
                        Arrays.asList(lhs),
                        Arrays.asList());
            }
            return result;
        }
        TempRegister lhs = this.munchExpression(binop.getLeft());
        TempRegister rhs = this.munchExpression(binop.getRight());
        if (binop.getOp() == InfixOperator.And) {
            this.emitOther("and `d0, `s0, `s1",
                    Arrays.asList(result),
                    Arrays.asList(lhs, rhs),
                    Arrays.asList());
        } else {
            this.emitOther("or `d0, `s0, `s1",
                    Arrays.asList(result),
                    Arrays.asList(lhs, rhs),
                    Arrays.asList());
        }
        return result;
    }
}
