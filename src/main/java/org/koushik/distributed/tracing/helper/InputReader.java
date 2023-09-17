package org.koushik.distributed.tracing.helper;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;
    private final PrintStream out;

    public InputReader(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void writeMessage(String message) {
        out.println(message);
    }
    public String readInputFromConsole(UserMessageEnum command) {
        writeMessage(command.message);
        return scanner.nextLine();
    }

    public String validateInput(String result) {
        while(result.isEmpty() || result.equals("\n")) {
            result = readInputFromConsole(UserMessageEnum.INPUT_PATH);
        }
        return result;
    }
}
