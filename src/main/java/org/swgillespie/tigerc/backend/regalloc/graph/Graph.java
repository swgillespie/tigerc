package org.swgillespie.tigerc.backend.regalloc.graph;

/**
 * Created by sean on 3/20/15.
 */
public interface Graph<V> {
    boolean hasVertex(V vertex);
    boolean hasEdge(V source, V sink);
    void addVertex(V vertex);
    void addEdge(V source, V sink);
}
