/**
 * RelayNeuronFactory.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.neurons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.illinois.abhayp4.brain_project.graphs.Graph;
import edu.illinois.abhayp4.brain_project.graphs.GraphNode;

public class RelayNeuronFactory {
    @JsonProperty("Graphs") private final List<Graph<RelayNeuron>> graphs;
    private final Map<RelayNeuron, List<RelayNeuron>> adjacency;

    public RelayNeuronFactory(
        @JsonProperty("Graphs") List<Graph<RelayNeuron>> graphs
    ) {
        this.graphs = graphs;
        adjacency = new HashMap<>();
        for (Graph<RelayNeuron> graph : graphs) {
            for (GraphNode<RelayNeuron> node : graph) {
                List<RelayNeuron> adjacentNodes = new ArrayList<>();
                for (GraphNode<RelayNeuron> adjNode : node.getAdjacentNodes()) {
                    adjacentNodes.add(adjNode.data);
                }
                adjacency.put(node.data, adjacentNodes);
            }
        }
    }

    public void build() {
        
    }
}