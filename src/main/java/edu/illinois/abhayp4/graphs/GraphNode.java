/**
 * GraphNode.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.graphs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
    generator = ObjectIdGenerators.IntSequenceGenerator.class,
    property = "@id"
)
public final class GraphNode<T> {
    @JsonProperty("@id") private int id;
    private final List<GraphNode<T>> adjacentNodes;
    @JsonIgnore public T data;

    public GraphNode(
        @JsonProperty("@id") int id
    ) {
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