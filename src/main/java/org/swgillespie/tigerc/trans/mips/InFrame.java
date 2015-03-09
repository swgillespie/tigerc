package org.swgillespie.tigerc.trans.mips;

/**
 * Created by sean on 3/8/15.
 */
final class InFrame extends MipsFrameAccess {
    private int offset;

    public InFrame(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
