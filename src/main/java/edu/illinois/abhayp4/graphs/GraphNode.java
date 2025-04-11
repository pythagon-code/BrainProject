/**
 * GraphNode.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class GraphNode<T> {
    private int nodeId;
    private final List<GraphNode<T>> adjacentNodes;
    @JsonIgnore public T data;

    public GraphNode(int nodeId) {
        this.nodeId = nodeId;
        adjacentNodes = new ArrayList<>();
    }

    public Iterable<GraphNode<T>> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNode(GraphNode<T> node) {
        if (adjacentNodes.contains(node)) {
            throw new IllegalArgumentException("Argument node is already adjacent.");
        }
        
        adjacentNodes.add(node);
        node.adjacentNodes.add(this);
    }

    @Override
    public String toString() {
        return "" + nodeId + ": " + adjacentNodes;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GraphNode<?>) {


            return adjacentNodes.equals(other) && (nodeId == ((GraphNode<?>) other).nodeId);
        }
        else {
            return false;
        }
    }
}