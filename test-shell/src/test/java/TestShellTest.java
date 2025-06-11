import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShellTest {

    TestShell shell;

    @BeforeEach
    void setUp() {
        shell = new TestShell();
    }

    private String getString(String input) {
        Scanner scanner = new Scanner(input);
        return shell.runTestShell(scanner);
    }

    @Test
    void read_Command_입력시_read_반환() {
        String expect = "read";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void write_Command_입력시_write_반환() {
        String expect = "write";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void fullread_Command_입력시_fullread_반환() {
        String expect = "fullread";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void fullwrite_Command_입력시_fullwrite_반환() {
        String expect = "fullwrite";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void help_Command_입력시_help_반환() {
        String expect = "help";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void exit_Command_입력시_exit_반환() {
        String expect = "exit";
        String actual = getString(expect + "\n");

        assertEquals(expect, actual);
    }

    @Test
    void null_Command_입력시_notCommand_반환() {
        String expect = "notCommand";
        String actual = getString("\n");

        assertEquals(expect, actual);
    }

    @Test
    void 없는_Command_입력시_notCommand_반환() {
        String expect = "notCommand";
        String actual = getString("wrongCmd\n");

        assertEquals(expect, actual);
    }
}