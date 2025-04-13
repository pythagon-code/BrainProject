/**
 * Graph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.graphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY
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
public sealed abstract class Graph<T> implements Iterable<GraphNode<T>>
    permits TreeGraph, WalkGraph, CompleteGraph, CustomGraph
{
    protected List<GraphNode<T>> nodes;

    public Graph(
        @JsonProperty("GraphData") List<T> graphData
    ) {
        nodes = new ArrayList<>();

        populateData(graphData, Graph.class);
    }

    protected void populateData(List<T> graphData, Class<?> clazz) {
        if (getClass() != clazz) {
            return;
        }
        
        if (nodes.size() != graphData.size()) {
            throw new IllegalArgumentException();
        }
        
        for (int i = 0; i < graphData.size(); i++) {
            nodes.get(i).data = graphData.get(i);
        }
    }

    protected GraphNode<T> addNode() {
        GraphNode<T> node = new GraphNode<>(nodes.size() + 1);
        nodes.add(node);
        return node;
    }

    public Iterator<GraphNode<T>> iterator() {
        return nodes.iterator();
    }

    @JsonGetter("GraphData")
    private List<T> getGraphData() {
        ArrayList<T> graphData = new ArrayList<>(nodes.size());
        for (GraphNode<T> node : nodes) {
            graphData.add(node.data);
        }
        return graphData;
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

    protected List<GraphNode<T>> getOuterNodes(int n_requested_outer_nodes) {return null;}
}