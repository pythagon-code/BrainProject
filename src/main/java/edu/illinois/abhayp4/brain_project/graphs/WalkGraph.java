/**
 * WalkGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

sealed class WalkGraph<T> extends Graph<T> permits CycleGraph {
    @JsonProperty("N") protected final int n;

    @JsonCreator
    public WalkGraph(
        @JsonProperty("N") int n,
        @JsonProperty("GraphData") List<T> graphData
    ) {
        super(graphData);

        if (n <= 0) {
            throw new IllegalArgumentException("N must be positive");
        }

        this.n = n;

        addNode();
        for (int i = 1; i < n; i++) {
            addNode().setAdjacentNode(nodes.getLast());
        }

        populateData(graphData, WalkGraph.class);
    }

    @Override
    protected List<GraphNode<T>> getOuterNodes(int nRequestedOuterNodes) {
        if (nRequestedOuterNodes <= 0) {
            throw new IllegalArgumentException();
        }

        if (nRequestedOuterNodes > n || (n - 1) % nRequestedOuterNodes != 0) {
            return null;
        }
        
        List<GraphNode<T>> outerNodes = new ArrayList<>(nRequestedOuterNodes);
        
        int step = (n - 1) / (nRequestedOuterNodes - 1);
        
        for (int i = 0; i < n; i += step) {
            outerNodes.add(nodes.get(i));
        }
        
        return outerNodes;
    }
}