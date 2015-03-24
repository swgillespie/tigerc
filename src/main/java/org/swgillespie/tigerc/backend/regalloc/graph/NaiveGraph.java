package org.swgillespie.tigerc.backend.regalloc.graph;

import org.swgillespie.tigerc.common.CompilerAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sean on 3/23/15.
 */
public class NaiveGraph<V> implements Graph<V> {
    private Map<V, Vertex> vertexMap;

    public NaiveGraph() {
        this.vertexMap = new HashMap<>();
    }

    @Override
    public boolean hasVertex(V vertex) {
        return this.vertexMap.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(V source, V sink) {
        Vertex sourceVertex = this.vertexMap.get(source);
        CompilerAssert.check(sourceVertex != null, "tried to query edge of vertex not in graph");
        return sourceVertex.hasEdgeTo(sink);
    }

    @Override
    public void addVertex(V vertex) {
        this.vertexMap.put(vertex, new Vertex(vertex));
    }

    @Override
    public void addEdge(V source, V sink) {
        Vertex sourceVertex = this.vertexMap.get(source);
        Vertex sinkVertex = this.vertexMap.get(source);
        CompilerAssert.check(sourceVertex != null, "tried to query edge of vertex not in graph");
        CompilerAssert.check(sinkVertex != null, "tried to query edge of vertex not in graph");
        sourceVertex.addOutgoingEdge(sink);
        sinkVertex.addIncomingEdge(source);
    }

    private class Vertex {
        private V data;
        private List<V> outgoingEdges;
        private List<V> incomingEdges;

        public Vertex(V data) {
            this.data = data;
            this.outgoingEdges = new ArrayList<>();
            this.incomingEdges = new ArrayList<>();
        }

        public void addOutgoingEdge(V sinkVertex) {
            this.outgoingEdges.add(sinkVertex);
        }

        public void addIncomingEdge(V sourceVertex) {
            this.incomingEdges.add(sourceVertex);
        }

        public boolean hasEdgeTo(V sinkVertex) {
            return this.outgoingEdges.contains(sinkVertex);
        }

        public int outgoingDegree() {
            return this.outgoingEdges.size();
        }

        public int incomingDegree() {
            return this.incomingEdges.size();
        }

        public List<V> incomingEdges() {
            return this.incomingEdges;
        }

        public List<V> outgoingEdges() {
            return this.outgoingEdges;
        }

        public List<V> allEdges() {
            List<V> allEdges = new ArrayList<>();
            allEdges.addAll(this.incomingEdges);
            allEdges.addAll(this.outgoingEdges);
            return allEdges;
        }

        public V getData() {
            return data;
        }
    }
}
