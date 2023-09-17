package org.koushik.distributed.tracing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.koushik.distributed.tracing.model.AllConnections;
import org.koushik.distributed.tracing.model.ConnectionDetails;
import org.koushik.distributed.tracing.model.Graph;
import org.koushik.distributed.tracing.service.GraphService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for Graph Service of distributed-tracing-test App.
 */
class TestGraphService {
    static GraphService graphService;
    static Graph graph;
    static AllConnections validGraphConn;

    static String validInput;
    static String invalidInput;

    @BeforeAll
    static void setupClass() {
        graphService = new GraphService();

        validInput = "AB5, BC4, CD8, DC9, DE10, AD5, CE2, EB3, AE7";
        invalidInput = "5AB, B4C, cd8, De6, ABC5";

        validGraphConn = graphService.parseInputServiceConnectionDetails(validInput);
        graph = graphService.createServiceGraph(validGraphConn);
    }

    @Test
    void test_validateGraphForNullOrBlankInput() {
        assertThat(false, allOf(is(graphService.validateGraphNodesInput(null)),
                                        is(graphService.validateGraphNodesInput(""))));
    }

    @Test
    void test_validateGraphForInvalidInput() {
        String[] allInvalidInputs = invalidInput.split(",");

        assertThat(false, allOf(
                is(graphService.validateGraphNodesInput(allInvalidInputs[0])),
                is(graphService.validateGraphNodesInput(allInvalidInputs[1])),
                is(graphService.validateGraphNodesInput(allInvalidInputs[2])),
                is(graphService.validateGraphNodesInput(allInvalidInputs[3])),
                is(graphService.validateGraphNodesInput(allInvalidInputs[4]))
        ));
    }

    @Test
    void test_validateGraphForValidInput() {
        assertThat(true, is(graphService.validateGraphNodesInput(validInput)));
    }

    @Test
    void test_parseInputForValidInputToCreateGraph() {
        String input = "AB5, BC4";
        List<ConnectionDetails> dummyConnections = new ArrayList<>();
        ConnectionDetails connectionDetail = new ConnectionDetails("A", "B", 5);
        dummyConnections.add(connectionDetail);
        connectionDetail = new ConnectionDetails("B", "C", 4);
        dummyConnections.add(connectionDetail);

        AllConnections result = graphService.parseInputServiceConnectionDetails(input);

        assertThat(result.allConnections(), samePropertyValuesAs(dummyConnections));

        assertThat(result.services(), containsInAnyOrder("A", "B", "C"));
    }

    @Test
    void test_whenTryToParseInvalidInputThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> graphService.parseInputServiceConnectionDetails(invalidInput));

        assertThat("Bad Input value(s)!", is(ex.getMessage()));
    }

    @Test
    void test_createGraphWithGivenValidUserInput() {
        // Arrange
        String inputToCreateGraph = validInput = "AB5, BC4, CD8, DE6";

        AllConnections result = graphService.parseInputServiceConnectionDetails(inputToCreateGraph);

        Graph dummyGraph = new Graph(new ArrayList<>(result.services()));
        dummyGraph.addAdjacentVertex("A", "B", 5);
        dummyGraph.addAdjacentVertex("B", "C", 4);
        dummyGraph.addAdjacentVertex("C", "D", 8);
        dummyGraph.addAdjacentVertex("D", "E", 6);

        // Act
        Graph serviceGraph = graphService.createServiceGraph(result);

        // Assert
        assertNotNull(serviceGraph);

        assertThat(dummyGraph, samePropertyValuesAs(serviceGraph));
    }

    @Test
    void test_getLatencyOfValidGraphConnection() {
        AllConnections result = graphService.parseInputServiceConnectionDetails(validInput);

        Graph graph = graphService.createServiceGraph(result);

        assertThat(10, is(graph.getLatency("D", "E")));

    }

    @Test
    void test_getLatencyOfInValidGraphConnection() {
        AllConnections result = graphService.parseInputServiceConnectionDetails(validInput);

        Graph graph = graphService.createServiceGraph(result);

        assertThat(-1, allOf(
                is(graph.getLatency("C", "B")),
                is(graph.getLatency("X", "Y")))
        );
    }

    @Test
    void test_getTotalAverageLatencyOfGivenTraces() {
        String traces = "A-B-C";

        assertThat("9", is(graphService.getTotalAverageLatencyOfGivenTraces(graph, traces)));
    }

    @Test
    void test_getTotalAverageLatencyOfGivenInvalidTraces() {
        String traces = "A-E-D";

        assertThat("NO SUCH TRACE", is(graphService.getTotalAverageLatencyOfGivenTraces(graph, traces)));
    }

    @Test
    void test_findNumberOfTracesWithGivenMaxKHops() {
        String source = "C", destination = "C";
        int maxHops = 3;

        assertThat(2, is(graphService.findNoOfTracesWithGivenMaxKHops(graph, source, destination, maxHops)));
    }

    @Test
    void test_findNumberOfTracesWithGivenMaxKHopsForNullOrEmptyNodes() {
        assertThat(-1, allOf(is(graphService.findNoOfTracesWithGivenMaxKHops(graph, null, null, 3)),
                is(graphService.findNoOfTracesWithGivenMaxKHops(graph, "", "", 3))));
    }

    @Test
    void test_findNumberOfTracesEqualToGivenHops() {
        String source = "C", destination = "C";
        int hops = 3;

        assertThat(1, is(graphService.findNoOfTracesEqualToGivenHops(graph, source, destination, hops)));
    }

    @Test
    void test_findNumberOfTracesEqualToGivenHopsForNullOrEmptyNodes() {
        assertThat(-1, allOf(is(graphService.findNoOfTracesEqualToGivenHops(graph, null, null, 3)),
                is(graphService.findNoOfTracesEqualToGivenHops(graph, "", "", 3))));
    }

    @Test
    void test_getShortestTraceByLatencyBetweenGivenNodes() {
        String source = "A", destination = "E";

        assertThat(7, is(graphService.getShortestTraceByLatencyBetweenGivenNodes(graph, source, destination)));
    }

    @Test
    void test_getShortestTraceByLatencyBetweenGivenNullOrEmptyNodes() {
        assertThat(-1, allOf(is(graphService.getShortestTraceByLatencyBetweenGivenNodes(graph, null, null)),
                is(graphService.getShortestTraceByLatencyBetweenGivenNodes(graph, "", ""))));
    }

    @Test
    void test_findNoOfPossibleTracesBetweenTwoNodesLessThanGivenLatency() {
        String source = "A", destination = "C";
        int latencyLimit = 25;

        assertThat(7, is(graphService.getPossibleTracesLessThanGivenLatency(graph, source, destination, latencyLimit)));
    }

    @Test
    void test_findNoOfPossibleTracesBetweenNullOrEmptyNodesOrNegativeLatency() {
        assertThat(-1, allOf(is(graphService.getPossibleTracesLessThanGivenLatency(graph, null, null, 25)),
                                is(graphService.getPossibleTracesLessThanGivenLatency(graph, "", "", 25)),
                                is(graphService.getPossibleTracesLessThanGivenLatency(graph, "A", "C", -1))));
    }

}
