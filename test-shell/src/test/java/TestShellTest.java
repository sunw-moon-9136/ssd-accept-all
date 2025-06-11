import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShellTest {
    public static final String INVALID_COMMAND = "INVALID COMMAND";
    TestShell shell;

    @BeforeEach
    void setUp() {
        shell = new TestShell();
    }

    private String getString(String input) {
        Scanner scanner = new Scanner(input);
        return shell.runTestShell(scanner);
    }

    @Nested
    class CommandInputTest {
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
            String actual = getString("\n");

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void 없는_Command_입력시_notCommand_반환() {
            String actual = getString("wrongCmd\n");

            assertEquals(INVALID_COMMAND, actual);
        }
    }

    @Nested
    class ValidationCmdTest {
        @Test
        void Command_포맷_검사_틀린_포맷_첫번째_항목_대문자() {
            String input = "READ 3";
            String actual = getString(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_틀린_포맷_두번째_항목_숫자가_아님() {
            String input = "read ee";
            String actual = getString(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_틀린_포맷_두번째_항목_숫자_범위_99() {
            String input = "READ 100";
            String actual = getString(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_각_arg_갯수_확인() {
            String input = "help 3 0xAAAABBBB";
            String actual = getString(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void read_Command_포맷_검사_올바른_포맷() {
            String expect = "read";

            String input = "read 3";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_포맷_검사_올바른_포맷() {
            String expect = "write";

            String input = "write 3 0xAAAABBBB";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_10글자() {
            String expect = "write";

            String input = "write 3 0xAAAAB";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_처음0x() {
            String expect = "write";

            String input = "write 3 SSAAAABBBB";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_소문자확인() {
            String expect = "write";

            String input = "write 3 0xaaaabbbb";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void fullread_Command_포맷_검사_올바른_포맷() {
            String expect = "fullread";

            String input = "fullread";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void fullwrite_Command_포맷_검사_올바른_포맷() {
            String expect = "fullwrite";

            String input = "fullwrite";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void help_Command_포맷_검사_올바른_포맷() {
            String expect = "help";

            String input = "help";
            String actual = getString(input);

            assertEquals(expect, actual);
        }

        @Test
        void exit_Command_포맷_검사_올바른_포맷() {
            String expect = "exit";

            String input = "exit";
            String actual = getString(input);

            assertEquals(expect, actual);
        }
    }
}