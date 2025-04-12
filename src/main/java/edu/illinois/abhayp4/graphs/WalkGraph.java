/**
 * WalkGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

sealed class WalkGraph<T> extends Graph<T> permits CycleGraph {
    @JsonCreator
    public WalkGraph(
        @JsonProperty("N") int n
    ) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be positive");
        }

        addNode();
        for (int i = 1; i < n; i++) {
            addNode().setAdjacentNode(nodes.getLast());
        }
    }
}