package org.swgillespie.tigerc.common;

/**
 * Created by sean on 3/3/15.
 */
public class Symbol {
    private String value;

    public Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "value='" + value + '\'' +
                '}';
    }
}
