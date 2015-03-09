package org.swgillespie.tigerc.trans.ir;

import org.swgillespie.tigerc.trans.TempLabel;

/**
 * Created by sean on 3/8/15.
 */
public final class PatchList {
    private TempLabel head;
    private PatchList tail;

    public PatchList(TempLabel head, PatchList tail) {
        this.head = head;
        this.tail = tail;
    }

    public TempLabel getHead() {
        return head;
    }

    public void setHead(TempLabel head) {
        this.head = head;
    }

    public PatchList getTail() {
        return tail;
    }

    public void setTail(PatchList tail) {
        this.tail = tail;
    }

    public static void patch(PatchList list, TempLabel label) {
        for (PatchList cursor = list; cursor != null; cursor = cursor.tail) {
            cursor.head = label;
        }
    }

    public static PatchList joinPatch(PatchList first, PatchList second) {
        PatchList cursor = first;
        while (cursor.tail != null) {
            cursor = cursor.tail;
        }
        cursor.tail = second;
        return cursor;
    }
}
