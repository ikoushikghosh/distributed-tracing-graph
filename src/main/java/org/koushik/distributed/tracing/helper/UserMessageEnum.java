package org.koushik.distributed.tracing.helper;

public enum UserMessageEnum {
    INPUT_PATH("Please give a valid path for input or press 'q' to quit:"),
    APP_EXIT_MESSAGE("Application exited by user."),
    INVALID_FILE_PATH_MESSAGE("Invalid input file path! Please try again or press 'q' to quit:");


    public final String message;
    private UserMessageEnum(String message) {
        this.message = message;
    }
}
