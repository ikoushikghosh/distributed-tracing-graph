package org.koushik.distributed.tracing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.koushik.distributed.tracing.helper.InputReader;
import org.koushik.distributed.tracing.helper.UserMessageEnum;
import org.koushik.distributed.tracing.controller.UserInputController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unit test for distributed-tracing-test App.
 */
class TestUserInput {
    private ByteArrayOutputStream outStream;
    private PrintStream out;
    private File file;
    private File invalidFile;
    @TempDir
    Path tempDir;
    static InputReader mockReader;
    static UserInputController inputService;

    @BeforeAll
    static void setupClass() {
        mockReader = mock(InputReader.class);
        inputService = new UserInputController(mockReader);
    }

    @BeforeEach
    void setupTest() {
        outStream = new ByteArrayOutputStream();
        out = new PrintStream(outStream);
        System.setOut(out);

        Path path;
        Path invalidFileExtPath;
        try {
            path = Files.createFile(tempDir.resolve("input.txt"));
            invalidFileExtPath = Files.createFile(tempDir.resolve("input.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file = path.toFile();
        invalidFile = invalidFileExtPath.toFile();
    }

    @Test
    void test_initApplication() {
        String input = "\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        InputReader reader = new InputReader(in, out);
        UserInputController service = new UserInputController(reader);

        assertEquals("", service.initApplication());
        assertEquals(UserMessageEnum.INPUT_PATH.message + "\n", outStream.toString());
    }

    @Test
    void test_processWhenUserInputUppercaseQToQuitApplication() {
        String emptyInput = "Q";
        InputStream in = new ByteArrayInputStream(emptyInput.getBytes());
        System.setIn(in);

        InputReader reader = new InputReader(in, out);
        UserInputController service = new UserInputController(reader);
        service.processInput(emptyInput);

        assertEquals(UserMessageEnum.APP_EXIT_MESSAGE.message + "\n", outStream.toString());
    }

    @Test
    void test_processWhenUserInputLowercaseQToQuitApplication() {
        String emptyInput = "q";
        InputStream in = new ByteArrayInputStream(emptyInput.getBytes());
        System.setIn(in);

        InputReader reader = new InputReader(in, out);
        UserInputController service = new UserInputController(reader);
        service.processInput(emptyInput);

        assertEquals(UserMessageEnum.APP_EXIT_MESSAGE.message + "\n", outStream.toString());
    }

    @Test
    void test_validInputPath() {
        assertThat(inputService.validateInputFilePath(file.getAbsolutePath()), is(true));
    }

    @Test
    void test_invalidInputPath() {
        String invalidPath = "c:/te:t/input.txt";

        assertThat(false, allOf(is(inputService.validateInputFilePath(invalidPath)),
                        is(inputService.validateInputFilePath(invalidFile.getAbsolutePath()))));
    }

    @AfterEach
    void tearDownTest() {
        System.setIn(System.in);
        System.setOut(System.out);
    }


}
