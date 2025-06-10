import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShellTest {

    @Test
    void runTestShell_없는_Command_입력시_notCommand_반환() {
        String input = "\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("notCommand", result);
    }

    @Test
    void runTestShell_readCommand_입력시_read_반환() {
        String input = "read\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("read", result);
    }

    @Test
    void runTestShell_writeCommand_입력시_write_반환() {
        String input = "write\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("write", result);
    }

    @Test
    void runTestShell_fullreadCommand_입력시_fullread_반환() {
        String input = "fullread\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("fullread", result);
    }

    @Test
    void runTestShell_fullwriteCommand_입력시_fullwrite_반환() {
        String input = "fullwrite\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("fullwrite", result);
    }

    @Test
    void runTestShell_helpCommand_입력시_help_반환() {
        String input = "help\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("help", result);
    }

    @Test
    void runTestShell_exitCommand_입력시_exit_반환() {
        String input = "exit\n";
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        TestShell shell = new TestShell();

        String result = shell.runTestShell(testInput);

        assertEquals("exit", result);
    }
}