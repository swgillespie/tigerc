package org.swgillespie.tigerc.trans;

import java.util.List;

/**
 * Created by sean on 3/5/15.
 */
public class TransLevel {
    private TransLevel parent;
    private StackFrame frame;
    private List<FrameAccess> formals;
    private List<FrameAccess> frameFormals;
    private FrameFactory frameFactory;
    private TempLabel label;

    private TransLevel(FrameFactory factory) {
        this.frameFactory = factory;
    }

    public TransLevel(TransLevel parent, TempLabel label, boolean[] formals) {
        this.frameFactory = parent.frameFactory;
        this.label = label;
        this.frame = this.frameFactory.createFrame(label, formals);
    }
}
