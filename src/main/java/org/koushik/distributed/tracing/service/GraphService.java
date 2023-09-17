package org.koushik.distributed.tracing.service;

import org.koushik.distributed.tracing.model.AllConnections;
import org.koushik.distributed.tracing.model.ConnectionDetails;
import org.koushik.distributed.tracing.model.ConnectionPair;
import org.koushik.distributed.tracing.model.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GraphService {
    private static final String VALID_GRAPH_CONN_REGEX_PATTERN = "^[A-Z]{2}\\d+$";
    public AllConnections parseInputServiceConnectionDetails(String input) {
        if(!validateGraphNodesInput(input))
            throw new IllegalArgumentException("Bad Input value(s)!");

        String[] connections = Arrays.stream(input.split(","))
                .map(String::trim).toArray(String[]::new);
        Set<String> allServices = new HashSet<>();
        List<ConnectionDetails> allConnections = new ArrayList<>();

        for (String conn : connections) {
            ConnectionDetails connection = new ConnectionDetails();
            connection.setSourceService(conn.substring(0, 1));
            connection.setDestService(conn.substring(1, 2));
            connection.setLatency(Integer.valueOf(conn.substring(2)));

            allServices.add(connection.getSourceService());
            allServices.add(connection.getDestService());

            allConnections.add(connection);
        }

        return new AllConnections(allServices, allConnections);
    }

    public boolean validateGraphNodesInput(String input) {
        if(input == null || input.isBlank()) return false;
        String[] connections = Arrays.stream(input.split(","))
                .map(String::trim).toArray(String[]::new);

        for (String conn: connections) {
            boolean matched = conn.matches(VALID_GRAPH_CONN_REGEX_PATTERN);
            if(!matched) return false;
        }
        return true;
    }

    public Graph createServiceGraph(AllConnections connections) {
        Graph serviceGraph = new Graph(new ArrayList<>(connections.services()));

        for(ConnectionDetails connection: connections.allConnections()) {
            serviceGraph.addAdjacentVertex(connection.getSourceService(), connection.getDestService(), connection.getLatency());
        }

        return serviceGraph;
    }

    public String getTotalAverageLatencyOfGivenTraces(Graph serviceGraph, String traces) {
        String[] allTraces = Arrays.stream(traces.split("-"))
                .map(String::trim).toArray(String[]::new);
        int totalWeight = 0;

        for (int nodeIdx = 1; nodeIdx < allTraces.length; nodeIdx++) {
            int currentWeight = serviceGraph.getLatency(allTraces[nodeIdx - 1], allTraces[nodeIdx]);

            if (currentWeight == -1) {
                return "NO SUCH TRACE";
            }

            totalWeight += currentWeight;
        }
        return String.valueOf(totalWeight);
    }

    public int findNoOfTracesWithGivenMaxKHops(Graph graph, String source, String destination, int maxHops) {
        if(isInvalidServiceNodeInput(source, destination)) return -1;

        List<String> currentPath = new ArrayList<>();

        currentPath.add(source);

        return findTracesWithMaxKHopsDFS(graph, source, destination, maxHops, 0, currentPath);
    }

    private int findTracesWithMaxKHopsDFS(Graph graph, String node, String destNode, int maxHops,
                                                 int currentTotalHops, List<String> paths) {
        if(currentTotalHops > maxHops) return 0;
        if(node.equals(destNode) && currentTotalHops > 0) {
            return 1;
        }

        int count = 0;
        Map<String, Integer> adjList = graph.getAdjacencyList().get(node);
        if(adjList != null && adjList.size() > 0) {
            for (Map.Entry<String, Integer> adjNode : adjList.entrySet()) {
                String neighbour = adjNode.getKey();
                paths.add(neighbour);
                count += findTracesWithMaxKHopsDFS(graph, neighbour, destNode, maxHops, currentTotalHops + 1, paths);
                paths.remove(paths.size() - 1);
            }
        }

        return count;
    }

    public int findNoOfTracesEqualToGivenHops(Graph graph, String source, String destination, int hops) {
        if(isInvalidServiceNodeInput(source, destination)) return -1;

        List<String> currentPath = new ArrayList<>();

        currentPath.add(source);

        return findTracesWithEqualToKHopsDFS(graph, source, destination, hops, 0, currentPath);
    }

    private int findTracesWithEqualToKHopsDFS(Graph graph, String node, String destNode, int hops,
                                              int currentTotalHops, List<String> paths) {
        if(currentTotalHops > hops) return 0;
        if(node.equals(destNode) && currentTotalHops > 0 && currentTotalHops == hops)  {
            return 1;
        }

        int count = 0;
        Map<String, Integer> adjList = graph.getAdjacencyList().get(node);
        if(adjList != null && adjList.size() > 0) {
            for (Map.Entry<String, Integer> adjNode : adjList.entrySet()) {
                String neighbour = adjNode.getKey();
                paths.add(neighbour);
                count += findTracesWithEqualToKHopsDFS(graph, neighbour, destNode, hops, currentTotalHops + 1, paths);
                paths.remove(paths.size() - 1);
            }
        }

        return count;
    }

    public int getShortestTraceByLatencyBetweenGivenNodes(Graph graph, String source, String destination) {
        if(isInvalidServiceNodeInput(source, destination)) return -1;

        int length = graph.getAdjacencyList().size();

        int[] shortestLatencyArr = new int[length];

        shortestLatencyArr = Arrays.stream(shortestLatencyArr).map(x -> Integer.MAX_VALUE).toArray();
        shortestLatencyArr[0] = 0;

        PriorityQueue<ConnectionPair> minLatencyHeap = new PriorityQueue<>(Comparator.comparingInt(ConnectionPair::latency));

        minLatencyHeap.add(new ConnectionPair(source, 0));

        while(!minLatencyHeap.isEmpty()) {
            ConnectionPair nodePair = minLatencyHeap.poll();
            int currentLatency = nodePair.latency();

            Map<String, Integer> adjacencyList = graph.getAdjacencyList().get(nodePair.serviceNode());
            if (adjacencyList != null && adjacencyList.size() > 0) {
                for (Map.Entry<String, Integer> adjNode : adjacencyList.entrySet()) {
                    int shortestArrIdx = adjNode.getKey().charAt(0) - 'A';
                    if (currentLatency + adjNode.getValue() < shortestLatencyArr[shortestArrIdx]) {
                        shortestLatencyArr[shortestArrIdx] = currentLatency + adjNode.getValue();
                        minLatencyHeap.add(new ConnectionPair(adjNode.getKey(), shortestLatencyArr[shortestArrIdx]));
                    }
                }
            }
        }

        return shortestLatencyArr[destination.charAt(0) - 'A'];
    }

    public int getPossibleTracesLessThanGivenLatency(Graph graph, String source, String destination, int latencyLimit) {
        if(isInvalidServiceNodeInput(source, destination) || latencyLimit < 0) return -1;

        List<String> currentPath = new ArrayList<>();

        currentPath.add(source);

        return getAllTracesWithinGivenLatencyByDFS(graph, source, destination, latencyLimit,0, currentPath);
    }

    private int getAllTracesWithinGivenLatencyByDFS(Graph graph, String node, String destination, int latencyLimit,
                                   int currentLatency, List<String> traces) {
        int count = 0;
        if(currentLatency >= latencyLimit) return count;
        if(node.equals(destination) && currentLatency > 0) {
            count++;
        }

        Map<String, Integer> adjList = graph.getAdjacencyList().get(node);
        if (adjList != null && adjList.size() > 0) {
            for (Map.Entry<String, Integer> adjNode : adjList.entrySet()) {
                String neighbour = adjNode.getKey();
                Integer neighbourLatency = adjNode.getValue();
                traces.add(neighbour);
                count += getAllTracesWithinGivenLatencyByDFS(graph, neighbour, destination, latencyLimit,
                        currentLatency + neighbourLatency, traces);
                traces.remove(traces.size() - 1);
            }
        }

        return count;
    }

    private boolean isInvalidServiceNodeInput(String source, String destination) {
        return source == null || source.isBlank() ||
                destination == null || destination.isBlank();
    }
}
