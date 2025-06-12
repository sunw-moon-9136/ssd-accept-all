package shell.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.Processor;
import shell.output.Output;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerTest {
    public static final String INVALID_COMMAND = "INVALID COMMAND";
    @Mock
    Processor mockProcessor;

    @Mock
    Output mockOutput;

    Manager shell;

    @BeforeEach
    void setUp() {
        shell = new Manager(mockProcessor, mockOutput);
    }

    private String getOutputResult(String input) {
        Scanner scanner = new Scanner(input);
        return shell.runTestShell(scanner);
    }

    @Nested
    class CommandInputTest {
        @Test
        void read_Command_입력시_read_반환() {
            String expect = "read";
            String actual = getOutputResult("read 3\n");

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_입력시_write_반환() {
            String expect = "write";
            String actual = getOutputResult("write 3 0xAAAABBBB\n");

            assertEquals(expect, actual);
        }

        @Test
        void fullread_Command_입력시_fullread_반환() {
            String expect = "fullread";
            String actual = getOutputResult("fullread\n");

            assertEquals(expect, actual);
        }

        @Test
        void fullwrite_Command_입력시_fullwrite_반환() {
            String expect = "fullwrite";
            String actual = getOutputResult("fullwrite 0xAAAABBBB\n");

            assertEquals(expect, actual);
        }

        @Test
        void help_Command_입력시_help_반환() {
            String expect = "help";
            String actual = getOutputResult("help\n");

            assertEquals(expect, actual);
        }

        @Test
        void exit_Command_입력시_exit_반환() {
            String expect = "exit";
            String actual = getOutputResult("exit\n");

            assertEquals(expect, actual);
        }

        @Test
        void null_Command_입력시_notCommand_반환() {
            String actual = getOutputResult("\n");

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void 없는_Command_입력시_notCommand_반환() {
            String actual = getOutputResult("wrongCmd\n");

            assertEquals(INVALID_COMMAND, actual);
        }
    }

    @Nested
    class ValidationCmdTest {
        @Test
        void Command_포맷_검사_틀린_포맷_첫번째_항목_대문자() {
            String input = "READ 3";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_틀린_포맷_두번째_항목_숫자가_아님() {
            String input = "read ee";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_틀린_포맷_두번째_항목_숫자_범위_99() {
            String input = "READ 100";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void Command_포맷_검사_각_arg_갯수_확인() {
            String input = "help 3 0xAAAABBBB";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_10글자() {
            String input = "write 3 0xAAAAB";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_처음0x() {
            String input = "write 3 SSAAAABBBB";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }

        @Test
        void write_Command_포맷_검사_세번째_항목_소문자확인() {
            String input = "write 3 0xaaaabbbb";
            String actual = getOutputResult(input);

            assertEquals(INVALID_COMMAND, actual);
        }
    }

    @Nested
    class ChainingTest {

        @Test
        void read_실행_true() {
            doReturn(true).when(mockProcessor).execute(anyString());

            String actual = getOutputResult("read 3\n");

            verify(mockProcessor, times(1)).execute(anyString());
        }

        @Test
        void write_실행_true() {
            doReturn(true).when(mockProcessor).execute(anyString());

            String actual = getOutputResult("write 3 0xAAAAFFFF\n");

            verify(mockProcessor, times(1)).execute(anyString());
        }

        @Test
        void fullread_실행_true() {
            doReturn(true).when(mockProcessor).execute(anyString());

            String actual = getOutputResult("fullread\n");

            verify(mockProcessor, times(100)).execute(anyString());
        }

        @Test
        void fullwrite_실행_true() {
            doReturn(true).when(mockProcessor).execute(anyString());

            String actual = getOutputResult("fullwrite 0xAAAAFFFF\n");

            verify(mockProcessor, times(100)).execute(anyString());
        }
    }

    @Nested
    class outputTest {
        @Test
        void write_정상입력시_output_checkResult_호출확인() {
            doReturn(true).when(mockProcessor).execute("write 3 0xABCDFFFF");
            doReturn("[write] DONE").when(mockOutput).checkResult("write", "3");

            getOutputResult("write 03 0xABCDFFFF\n");

            verify(mockOutput).checkResult("write", "3");
        }

        @Test
        void read_정상입력시_output_checkResult_호출확인() {
            doReturn(true).when(mockProcessor).execute("read 3");
            doReturn("[read] LBA 03 : 0xABCDFFFF").when(mockOutput).checkResult("read", "3");

            String actual = getOutputResult("read 3\n");

            verify(mockOutput).checkResult("read", "3");
            assertEquals("[read] LBA 03 : 0xABCDFFFF", actual);

        }
    }
}