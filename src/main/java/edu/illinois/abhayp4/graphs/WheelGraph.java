/**
 * WalkGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class WheelGraph<T> extends CycleGraph<T> {
    @JsonCreator
    public WheelGraph(
        @JsonProperty("N") int n,
        @JsonProperty("GraphData") List<T> graphData
    ) {
        super(n - 1, graphData);

        if (n <= 3) {
            throw new IllegalArgumentException();
        }

        GraphNode<T> center = addNode();
        for (GraphNode<T> node : nodes) {
            center.setAdjacentNode(node);
        }

        populateData(graphData, WheelGraph.class);
    }

    @Override
    protected List<GraphNode<T>> getOuterNodes(int nRequestedOuterNodes) {
        if (nRequestedOuterNodes == n) {
            return new ArrayList<>(nodes);
        }

        return super.getOuterNodes(nRequestedOuterNodes);
    }
}