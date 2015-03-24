package org.swgillespie.tigerc.trans;

import org.swgillespie.tigerc.common.InternalCompilerException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sean on 3/8/15.
 */
public final class Level {
    private StackFrame frame;
    private Level parent;

    public static Level outermost(TempFactory factory, StackFrameFactory stackFactory) {
        return new Level(null, factory.newNamedLabel("outermost"), new ArrayList<>(), stackFactory);
    }

    public Level(Level parent, TempLabel name, List<Boolean> formals, StackFrameFactory factory) {
        formals.add(0, true); // static link parameter, which always escapes
        this.frame = factory.newFrame(name, formals);
        this.parent = parent;
    }

    public List<Access> getFormals() {
        return frame.getFormals()
                .stream()
                .map(i -> new Access(this, i))
                .collect(Collectors.toList());
    }

    public Access allocLocal(boolean escape) {
        return new Access(this, frame.allocLocal(escape));
    }

    public StackFrame getFrame() {
        return frame;
    }

    public Level getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "level: " + frame.getName().getName();
    }
}
