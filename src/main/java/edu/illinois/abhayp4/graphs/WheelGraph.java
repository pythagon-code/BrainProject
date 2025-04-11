/**
 * WalkGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class WheelGraph<T> extends CycleGraph<T> {
    @JsonCreator
    public WheelGraph(
        @JsonProperty("N") int n
    ) {
        super(n);
        GraphNode<T> center = addNode();
        for (GraphNode<T> node : nodes) {
            center.setAdjacentNode(node);
        }
    }
}