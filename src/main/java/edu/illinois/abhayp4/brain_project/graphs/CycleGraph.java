/**
 * CycleGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

sealed class CycleGraph<T> extends WalkGraph<T> permits WheelGraph {
    @JsonCreator
    public CycleGraph(
        @JsonProperty("N") int n,
        @JsonProperty("GraphData") List<T> graphData
    ) {
        super(n, graphData);

        if (n <= 2) {
            throw new IllegalArgumentException();
        }
        
        nodes.getFirst().setAdjacentNode(nodes.getLast());

        populateData(graphData, CycleGraph.class);
    }

    @Override
    protected List<GraphNode<T>> getOuterNodes(int nRequestedOuterNodes) {
        if (nRequestedOuterNodes <= 0) {
            throw new IllegalArgumentException();
        }

        if (nRequestedOuterNodes > n || n % nRequestedOuterNodes != 0) {
            return null;
        }
        
        List<GraphNode<T>> outerNodes = new ArrayList<>(nRequestedOuterNodes);
        
        int step = n / nRequestedOuterNodes;
        
        for (int i = 0; i < outerNodes.size(); i += step) {
            outerNodes.add(nodes.get(i));
        }
        
        return outerNodes;
    }
}