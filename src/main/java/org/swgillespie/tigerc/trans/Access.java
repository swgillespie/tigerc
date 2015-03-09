package org.swgillespie.tigerc.trans;

/**
 * Created by sean on 3/8/15.
 */
public final class Access {
    private Level level;
    private FrameAccess access;

    public Access(Level level, FrameAccess access) {
        this.level = level;
        this.access = access;
    }

    public Level getLevel() {
        return level;
    }

    public FrameAccess getAccess() {
        return access;
    }
}
