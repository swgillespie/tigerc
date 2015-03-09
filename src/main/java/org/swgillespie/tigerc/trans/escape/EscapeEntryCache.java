package org.swgillespie.tigerc.trans.escape;

import org.swgillespie.tigerc.ast.DeclarationNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sean on 3/8/15.
 */
public final class EscapeEntryCache {
    private Map<DeclarationNode, EscapeEntry> entries;

    public EscapeEntryCache() {
        this.entries = new HashMap<>();
    }

    public void put(DeclarationNode node, EscapeEntry entry) {
        this.entries.put(node, entry);
    }

    public EscapeEntry get(DeclarationNode node) {
        return this.entries.get(node);
    }
}
