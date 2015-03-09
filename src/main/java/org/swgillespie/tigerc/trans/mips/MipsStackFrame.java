package org.swgillespie.tigerc.trans.mips;

import org.swgillespie.tigerc.trans.FrameAccess;
import org.swgillespie.tigerc.trans.StackFrame;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.TempLabel;

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
        formals = formalEscapes
                .stream()
                .map(b -> (MipsFrameAccess) this.allocLocal(b))
                .collect(Collectors.toList());
    }

    @Override
    public List<? extends FrameAccess> getFormals() {
        return formals;
    }

    @Override
    public FrameAccess allocLocal(boolean escape) {
        if (escape) {
            MipsFrameAccess access = MipsFrameAccess.inFrame(this.currentOffset);
            this.currentOffset -= 4;
            return access;
        }
        return MipsFrameAccess.inRegister(tempFactory.newTemp());
    }
}
