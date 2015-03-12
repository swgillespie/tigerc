package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.StackFrame;
import org.swgillespie.tigerc.trans.ir.IRStatement;

/**
 * Created by sean on 3/11/15.
 */
public class ProcFragment extends Fragment {
    private IRStatement body;
    private StackFrame frame;

    public ProcFragment(IRStatement body, StackFrame frame) {
        this.body = body;
        this.frame = frame;
    }

    public IRStatement getBody() {
        return body;
    }

    public StackFrame getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        return "ProcFragment{" +
                "body=" + body +
                ", frame=" + frame +
                '}';
    }
}
