package org.swgillespie.tigerc.ast;

/**
 * Created by sean on 2/27/15.
 */
public final class TextSpan {
    private TextPosition start;
    private TextPosition stop;

    public TextSpan(TextPosition start, TextPosition stop) {
        this.start = start;
        this.stop = stop;
    }

    public TextPosition getStart() {
        return start;
    }

    public void setStart(TextPosition start) {
        this.start = start;
    }

    public TextPosition getStop() {
        return stop;
    }

    public void setStop(TextPosition stop) {
        this.stop = stop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextSpan textSpan = (TextSpan) o;

        if (!start.equals(textSpan.start)) return false;
        if (!stop.equals(textSpan.stop)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + stop.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TextSpan{" +
                "start=" + start +
                ", stop=" + stop +
                '}';
    }
}
