import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShellTest {

    TestShell shell;

    @BeforeEach
    void setUp() {
        shell = new TestShell();
    }

    @Test
    void runTestShell_없는_Command_입력시_notCommand_반환() {
        String expect = "notCommand";
        String actual = getString("\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_readCommand_입력시_read_반환() {
        String expect = "read";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_writeCommand_입력시_write_반환() {
        String expect = "write";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_fullreadCommand_입력시_fullread_반환() {
        String expect = "fullread";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_fullwriteCommand_입력시_fullwrite_반환() {
        String expect = "fullwrite";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_helpCommand_입력시_help_반환() {
        String expect = "help";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void runTestShell_exitCommand_입력시_exit_반환() {
        String expect = "exit";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    private String getString(String input) {
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        return shell.runTestShell(testInput);
    }
}