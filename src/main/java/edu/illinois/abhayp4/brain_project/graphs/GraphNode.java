/**
 * GraphNode.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public final class GraphNode<T> {
    private int id;
    private final List<GraphNode<T>> adjacentNodes;
    public T data;

    public GraphNode(int id) {
        this.id = id;
        adjacentNodes = new ArrayList<>();
    }

    public Iterable<GraphNode<T>> getAdjacentNodes() {
        return adjacentNodes;
    }

    void setAdjacentNode(GraphNode<T> node) {
        if (adjacentNodes.contains(node)) {
            return;
        }
        adjacentNodes.add(node);
        node.adjacentNodes.add(this);
    }

    @Override
    public String toString() {
        return "" + id + ": " + adjacentNodes;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GraphNode<?>) {
            return adjacentNodes.equals(other) && (id == ((GraphNode<?>) other).id);
        }
        else {
            return false;
        }
    }
}