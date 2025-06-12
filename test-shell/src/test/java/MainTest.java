import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.manager.Manager;
import utils.Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainTest {
    private static final String INVALID_COMMAND = "INVALID COMMAND";

    @Mock
    Manager mockManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Main.manager = mockManager;  // mock 주입
    }

    private Scanner getScanner(String text) {
        ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes());
        return new Scanner(input);
    }

    @Test
    void Command_포맷_검사_틀린_포맷_첫번째_항목_대문자() {
        Scanner scanner = getScanner("Read 3\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void Command_포맷_검사_틀린_포맷_두번째_항목_숫자가_아님() {
        Scanner scanner = getScanner("read ee\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void Command_포맷_검사_틀린_포맷_두번째_항목_숫자_범위_99() {
        Scanner scanner = getScanner("READ 100\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void Command_포맷_검사_각_arg_갯수_확인() {
        Scanner scanner = getScanner("help 3 0xAAAABBBB\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void write_Command_포맷_검사_세번째_항목_10글자() {
        Scanner scanner = getScanner("write 3 0xAAAAB\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void write_Command_포맷_검사_세번째_항목_처음0x() {
        Scanner scanner = getScanner("write 3 SSAAAABBBB\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void write_Command_포맷_검사_세번째_항목_소문자확인() {
        Scanner scanner = getScanner("write 3 0xaaaabbbb\nexit\n");

        // System.out 캡처
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // 실행
        Main.run(scanner);

        // 원래 출력으로 복원
        System.setOut(originalOut);

        String output = outContent.toString();
        assertTrue(output.contains("INVALID COMMAND"));
    }

    @Test
    void null_Command_입력시_notCommand_반환() {
        Scanner scanner = getScanner("\nexit\n");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Main.run(scanner);

        System.setOut(originalOut); // 출력 원복
        String output = outContent.toString();
        assertTrue(output.contains(INVALID_COMMAND));
    }

    @Test
    void 없는_Command_입력시_notCommand_반환() {
        Scanner scanner = getScanner("aaaa\nexit\n");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Main.run(scanner);

        System.setOut(originalOut); // 출력 원복
        String output = outContent.toString();
        assertTrue(output.contains(INVALID_COMMAND));
    }

    @Test
    void fullread_Command_입력시_value_반환_100번_실행() {
        doReturn("0xAAAAAAAA").when(mockManager).read(anyInt());
        Scanner scanner = getScanner("fullread\nexit\n");

        Main.run(scanner);

        verify(mockManager, times(100)).read(anyInt());
    }

    @Test
    void fullwrite_Command_입력시_true_반환_100번_실행() {
        doReturn(true).when(mockManager).write(anyInt(), anyString());
        Scanner scanner = getScanner("fullwrite 0xAAAAFFFF\nexit\n");

        Main.run(scanner);

        verify(mockManager, times(100)).write(anyInt(), anyString());
    }

    @Test
    void help_Command_입력_시_HELP_TEXT_출력됨() {
        Scanner scanner = getScanner("help\nexit\n");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Main.run(scanner);

        System.setOut(originalOut); // 출력 원복
        String output = outContent.toString();
        assertTrue(output.contains(Common.HELP_TEXT));
    }
}