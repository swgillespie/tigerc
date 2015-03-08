package org.swgillespie.tigerc.ast;

/**
 * Created by sean on 2/27/15.
 */
public final class TextPosition {
    private int line;
    private int col;

    public TextPosition(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextPosition that = (TextPosition) o;

        if (col != that.col) return false;
        if (line != that.line) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "TextPosition{" +
                "line=" + line +
                ", col=" + col +
                '}';
    }
}
