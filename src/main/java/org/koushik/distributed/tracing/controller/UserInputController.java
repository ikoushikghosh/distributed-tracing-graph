package org.koushik.distributed.tracing.controller;

import org.koushik.distributed.tracing.helper.HelperUtil;
import org.koushik.distributed.tracing.helper.InputReader;
import org.koushik.distributed.tracing.helper.UserMessageEnum;
import org.koushik.distributed.tracing.model.AllConnections;
import org.koushik.distributed.tracing.model.Graph;
import org.koushik.distributed.tracing.service.GraphService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserInputController {
    private final InputReader reader;

    public UserInputController(InputReader reader) {
        this.reader = reader;
    }

    public String initApplication() {
        // initialize the application with asking user to provide a valid input file path
        return reader.readInputFromConsole(UserMessageEnum.INPUT_PATH);
    }

    public void processInput(String userInput) {
        // validate 1. input file path
        // 2. if the file exist or not
        // 3. if the file is .txt file or not
        userInput = reader.validateInput(userInput);

        switch (userInput.trim()) {
            // press 'Q' or 'q' to exit from the application
            case "Q", "q" -> reader.writeMessage(UserMessageEnum.APP_EXIT_MESSAGE.message);
            default -> {
                // validate and ask user to provide valid file details until getting correct input
                // or give the option to quit the application
                while (!userInput.equalsIgnoreCase("q") && !validateInputFilePath(userInput)) {
                    userInput = reader.readInputFromConsole(UserMessageEnum.INVALID_FILE_PATH_MESSAGE);
                }

                if(userInput.equalsIgnoreCase("q")) {
                    reader.writeMessage(UserMessageEnum.APP_EXIT_MESSAGE.message);
                    return;
                }

                // execute and calculate connection details with reading file line by line
                readFileAndExecuteGraphService(userInput);
            }
        }
    }

    public boolean validateInputFilePath(String filePath) {
        return HelperUtil.isValidPath(filePath);
    }

    private void readFileAndExecuteGraphService(String filePath) {
        try(BufferedReader buffReader = new BufferedReader(new FileReader(filePath))) {
            String line = buffReader.readLine();
            GraphService graphService = new GraphService();

            while (line != null) {
                AllConnections connections = graphService.parseInputServiceConnectionDetails(line);
                Graph graph = graphService.createServiceGraph(connections);

                // 1. The average latency of the trace A-B-C.
                String input = "A-B-C";
                String calculatedAvgLatency = graphService.getTotalAverageLatencyOfGivenTraces(graph, input);
                reader.writeMessage(calculatedAvgLatency);

                // 2. The average latency of the trace A-D.
                input = "A-D";
                calculatedAvgLatency = graphService.getTotalAverageLatencyOfGivenTraces(graph, input);
                reader.writeMessage(calculatedAvgLatency);

                // 3. The average latency of the trace A-D-C.
                input = "A-D-C";
                calculatedAvgLatency = graphService.getTotalAverageLatencyOfGivenTraces(graph, input);
                reader.writeMessage(calculatedAvgLatency);

                // 4. The average latency of the trace A-E-B-C-D.
                input = "A-E-B-C-D";
                calculatedAvgLatency = graphService.getTotalAverageLatencyOfGivenTraces(graph, input);
                reader.writeMessage(calculatedAvgLatency);

                // 5. The average latency of the trace A-E-D.
                input = "A-E-D";
                calculatedAvgLatency = graphService.getTotalAverageLatencyOfGivenTraces(graph, input);
                reader.writeMessage(calculatedAvgLatency);

                // 6. The number of traces originating in service C and ending in service C with a maximum of
                //    3 hops.
                String source = "C";
                String destination = "C";
                int noOfHops = 3;
                int noOfTraces = graphService.findNoOfTracesWithGivenMaxKHops(graph, source, destination, noOfHops);
                reader.writeMessage(String.valueOf(noOfTraces));

                // 7. The number of traces originating in A and ending in C with exactly 4 hops.
                source = "A";
                destination = "C";
                noOfHops = 4;
                noOfTraces = graphService.findNoOfTracesEqualToGivenHops(graph, source, destination, noOfHops);
                reader.writeMessage(String.valueOf(noOfTraces));

                // 8. The length of the shortest trace (in terms of latency) between A and C.
                source = "A";
                destination = "C";
                int shortestTrace = graphService.getShortestTraceByLatencyBetweenGivenNodes(graph, source, destination);
                reader.writeMessage(String.valueOf(shortestTrace));

                // 9. The length of the shortest trace (in terms of latency) between B and B.
                source = "B";
                destination = "B";
                shortestTrace = graphService.getShortestTraceByLatencyBetweenGivenNodes(graph, source, destination);
                reader.writeMessage(String.valueOf(shortestTrace));

                // 10. The number of different traces from C to C with an average latency of less than 30.
                source = "C";
                destination = "C";
                int latencyLimit = 30;
                int noOfPossibleTraces = graphService.getPossibleTracesLessThanGivenLatency(graph, source, destination, latencyLimit);
                reader.writeMessage(String.valueOf(noOfPossibleTraces));

                // read next line and execute
                line = buffReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while try to reading the file from given file path. Exception is - " + e.getMessage());
        }
    }
}
