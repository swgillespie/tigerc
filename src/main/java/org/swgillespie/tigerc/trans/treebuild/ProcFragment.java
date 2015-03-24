package org.swgillespie.tigerc.trans.treebuild;

import org.swgillespie.tigerc.trans.StackFrame;
import org.swgillespie.tigerc.trans.ir.IRStatement;

/**
 * Created by sean on 3/11/15.
 */
public class ProcFragment extends Fragment {
    private IRStatement body;
    private StackFrame frame;
    private boolean isToplevel;

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

    public void setBody(IRStatement body) {
        this.body = body;
    }

    public void setFrame(StackFrame frame) {
        this.frame = frame;
    }

    public boolean isToplevel() {
        return isToplevel;
    }

    public void setToplevel(boolean isToplevel) {
        this.isToplevel = isToplevel;
    }

    @Override
    public String toString() {
        return "ProcFragment{" +
                "body=" + body +
                ", frame=" + frame +
                '}';
    }
}
