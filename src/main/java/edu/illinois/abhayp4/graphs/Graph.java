/**
 * Graph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@name"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PerfectTreeGraph.class),
    @JsonSubTypes.Type(value = FractalTreeGraph.class),
    @JsonSubTypes.Type(value = CompleteGraph.class),
    @JsonSubTypes.Type(value = WalkGraph.class),
    @JsonSubTypes.Type(value = CycleGraph.class),
    @JsonSubTypes.Type(value = WheelGraph.class),
    @JsonSubTypes.Type(value = CustomGraph.class)
})
sealed abstract class Graph<T> permits PerfectTreeGraph, FractalTreeGraph, WalkGraph, CompleteGraph, CustomGraph {
    @JsonIgnore protected List<GraphNode<T>> nodes;

    public Graph() {
        nodes = new ArrayList<>();
    }

    protected GraphNode<T> addNode() {
        GraphNode<T> node = new GraphNode<>(nodes.size() + 1);
        nodes.add(node);
        return node;
    }

    public Iterable<GraphNode<T>> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + nodes.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Graph<?>) {
            return nodes.equals(((Graph<?>) other).nodes);
        }
        else {
            return false;
        }
    }
}