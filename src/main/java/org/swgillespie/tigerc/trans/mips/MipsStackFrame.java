package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.common.CompilerAssert;
import org.swgillespie.tigerc.trans.*;
import org.swgillespie.tigerc.trans.ir.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/8/15.
 */
public final class MipsStackFrame extends StackFrame {
    private int currentOffset;
    private TempFactory tempFactory;
    private List<MipsFrameAccess> formals;

    protected MipsStackFrame(TempLabel name, List<Boolean> formalEscapes, TempFactory tempFactory) {
        super(name, formalEscapes);
        this.tempFactory = tempFactory;
        this.currentOffset = 0;
        this.formals = new ArrayList<>();
        formalEscapes.forEach(this::allocLocal);
    }

    @Override
    public List<? extends FrameAccess> getFormals() {
        return formals;
    }

    @Override
    public FrameAccess allocLocal(boolean escape) {
        MipsFrameAccess access;
        if (escape) {
            access = MipsFrameAccess.inFrame(this.currentOffset);
            this.currentOffset -= MipsConstants.wordSize;
        } else {
            access = MipsFrameAccess.inRegister(tempFactory.newTemp());
        }
        formals.add(access);
        return access;
    }

    @Override
    public TempRegister framePointer() {
        // for Mips, the frame pointer always lives in the $fp register.
        return ((MipsTempFactory)tempFactory).FP;
    }

    @Override
    public TempRegister returnValue() {
        // for Mips, the return value always lives in the $ra register.
        return ((MipsTempFactory)tempFactory).RA;
    }

    @Override
    public IRStatement procEntryExit(IRStatement statement) {
        MipsTempFactory mts = (MipsTempFactory)tempFactory;
        IRStatement prolog = statement;
        List<TempRegister> argumentRegisters = mts.argumentRegisters();
        List<FrameAccess> savedRegisters = new ArrayList<>();
        List<TempRegister> calleeSavedRegisters = mts.calleeSaveRegisters();
        // first, we need to save all callee-saved registers.
        for (TempRegister calleeSavedReg : calleeSavedRegisters) {
            FrameAccess access = this.allocLocal(false);
            savedRegisters.add(access);
            IRExpression saveExpr = access.toExpression(new IRTemp(mts.FP), tempFactory);
            IRStatement move = new IRMoveMem(saveExpr, MipsConstants.wordSize, new IRTemp(calleeSavedReg));
            prolog = new IRSeq(move, prolog);
        }

        // next, we need to load all argument registers.
        // mips passes the first 4 arguments in $a0, $a1, $a2, and $a3.
        // Before we begin our function, we have to "move" our parameters from
        // these registers to the temporary locations that Trans has set up for them.
        // This is known in the Tiger Book as a "view shift".
        CompilerAssert.check(formals.size() <= 4, "can't pass more than 4 parameters yet");
        for (int i = 0; i < formals.size(); i++) {
            IRExpression loadExpr = formals.get(i).toExpression(new IRTemp(mts.FP), tempFactory);
            IRStatement move = new IRMoveMem(loadExpr, MipsConstants.wordSize, new IRTemp(argumentRegisters.get(i)));
            prolog = new IRSeq(move, prolog);
        }

        // finally, after the function is complete, we need to restore all callee-saved registers.
        for (int i = 0; i < calleeSavedRegisters.size(); i++) {
            FrameAccess savedReg = savedRegisters.get(i);
            IRExpression loadExpr = savedReg.toExpression(new IRTemp(mts.FP), tempFactory);
            IRStatement move = new IRMoveTemp(new IRTemp(calleeSavedRegisters.get(i)),
                    new IRMem(loadExpr, MipsConstants.wordSize));
            prolog = new IRSeq(prolog, move);
        }

        return prolog;
    }

    @Override
    public String toString() {
        return "MipsStackFrame{" +
                "currentOffset=" + currentOffset +
                ", tempFactory=" + tempFactory +
                ", formals=" + formals +
                '}';
    }
}
