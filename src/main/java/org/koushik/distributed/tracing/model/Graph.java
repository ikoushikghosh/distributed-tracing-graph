package org.koushik.distributed.tracing.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    Map<String, Map<String, Integer>> adjacencyList;

    public Graph(List<String> nodes) {
        this.adjacencyList = new HashMap<>();

        for(String node: nodes) {
            this.adjacencyList.putIfAbsent(node, new HashMap<>());
        }
    }

    public void addAdjacentVertex(String sourceNode, String destNode, Integer weight) {
        this.adjacencyList.get(sourceNode).put(destNode, weight);
    }

    public int getLatency(String sourceNode, String destNode) {
        Map<String, Integer> adjacentNodes = this.adjacencyList.get(sourceNode);

        if(adjacentNodes!= null &&
                adjacentNodes.size() > 0 &&
                    adjacentNodes.containsKey(destNode)) {
            return adjacentNodes.get(destNode);
        }

        return -1;
    }

    public Map<String, Map<String, Integer>> getAdjacencyList() {
        return adjacencyList;
    }
}
