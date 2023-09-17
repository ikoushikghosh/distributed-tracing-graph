package org.koushik.distributed.tracing;

import org.koushik.distributed.tracing.helper.InputReader;
import org.koushik.distributed.tracing.controller.UserInputController;


/**
 * Distributed Tracing service!
 *
 */
public class App {

    public static void main(String[] args) {
        UserInputController inputService = new UserInputController(new InputReader(System.in, System.out));
        String inputReceived = inputService.initApplication();
        inputService.processInput(inputReceived);
    }

}
