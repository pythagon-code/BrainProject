/**
 * CycleGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

sealed class CycleGraph<T> extends WalkGraph<T> permits WheelGraph {
    @JsonCreator
    public CycleGraph(
        @JsonProperty("N") int n
    ) {
        super(n);
        nodes.getFirst().setAdjacentNode(nodes.getLast());
    }
}