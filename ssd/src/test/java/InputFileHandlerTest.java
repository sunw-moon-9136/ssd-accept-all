import SSD.InputFileHandler;
import SSD.InputHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputFileHandlerTest {

    public InputHandler inputHandler;

    String BUFFER_PATH = "buffer";

    @BeforeEach
    void setUp() {
        inputHandler = new InputFileHandler();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BUFFER_PATH));
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            String BUFFER_PATH = "buffer";
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BUFFER_PATH));
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (Exception e) {
        }
    }

    @Test
    void WRITE_IGNORE_AND_ERASE_MERGE() {
        inputHandler.add("E 0 6");
        inputHandler.add("W 3 0xABCDABCD");
        inputHandler.add("E 0 4");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 0 6"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void test1() {
        inputHandler.add("W 33 0x33333333");
        inputHandler.add("E 43 10");
        inputHandler.add("W 44 0x33333333");
        inputHandler.add("W 33 0x44444444");

        List<String> answer = inputHandler.flush();
    }


    @Test
    void IGNORE_COMMAND1() {
        inputHandler.add("W 20 0xABCDABCD");
        inputHandler.add("W 21 0x12341234");
        inputHandler.add("W 20 0xEEEEFFFF");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "W 21 0x12341234",
                "W 20 0xEEEEFFFF"
        );
        assertIterableEquals(expected, answer);
    }


    @Test
    void IGNORE_COMMAND2() {
        inputHandler.add("E 18 3");
        inputHandler.add("W 21 0x12341234");
        inputHandler.add("E 18 5");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 18 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void MERGE_ERASE_TEST() {
        inputHandler.add("W 20 0xABCDABCD");
        inputHandler.add("E 10 4");
        inputHandler.add("E 12 3");

        List<String> answer = inputHandler.flush();

        List<String> expected = List.of(
                "W 20 0xABCDABCD",
                "E 10 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void FAST_READ_TEST() {
        inputHandler.add("W 10 0xABCDABCD");
        inputHandler.add("E 10 4");
        inputHandler.add("W 11 0xABCDABCD");
        assertThat(inputHandler.read(10)).isEqualTo("0x00000000");
        assertThat(inputHandler.read(11)).isEqualTo("0xABCDABCD");
    }

    @Test
    void BUFFER에_없는_값_FAST_READ_TEST() {
        assertThat(inputHandler.read(10)).isEqualTo("");
        assertThat(inputHandler.read(11)).isEqualTo("");
    }

    @Test
    void ERASE_SIZE_0일때_버퍼_미생성() {
        inputHandler.add("E 10 0");
        List<String> answer = inputHandler.flush();
        assertTrue(answer.isEmpty());
    }

    @Test
    void ERASE_SIZE_1이_존재할때_동일주소_WRITE() {
        inputHandler.add("E 10 1");
        inputHandler.add("W 10 0xABCDABCD");
        List<String> answer = inputHandler.flush();
        for (String str : answer) {
            System.out.println(str);
        }
        List<String> expected = List.of(
                "W 10 0xABCDABCD"
        );
        assertIterableEquals(expected, answer);

    }

    @Test
    void WRITE가_ERASE_START_부분이면_ERASE범위_조절() {
        inputHandler.add("E 10 5");
        inputHandler.add("W 10 0xABCDABCD");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "W 10 0xABCDABCD",
                "E 11 4"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void WRITE가_ERASE_END_부분이면_ERASE범위_조절() {
        inputHandler.add("E 10 5");
        inputHandler.add("W 14 0xABCDABCD");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "W 14 0xABCDABCD",
                "E 10 4"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_중복_범위_삽입() {
        inputHandler.add("E 0 5");
        inputHandler.add("E 2 1");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 0 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_경계_값_체크() {
        inputHandler.add("E 10 1");
        inputHandler.add("E 9 3");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 9 3"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_경계_값_체크2() {
        inputHandler.add("E 40 1");
        inputHandler.add("E 39 5");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 39 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_경계_값_체크3() {
        inputHandler.add("E 10 1");
        inputHandler.add("E 9 3");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 9 3"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_경계_값_체크4() {
        inputHandler.add("E 39 1");
        inputHandler.add("E 39 6");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 39 6"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_경계_값_체크5() {
        inputHandler.add("E 40 1");
        inputHandler.add("E 39 5");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "E 39 5"
        );
        assertIterableEquals(expected, answer);
    }


    @Test
    void 연속값이_하나씩_WRITE_되는_경우() {
        inputHandler.add("E 1 3");
        inputHandler.add("W 2 0x33333333");
        inputHandler.add("W 3 0x33333333");
        inputHandler.add("W 1 0x33333333");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "W 1 0X33333333",
                "W 2 0X33333333",
                "W 3 0X33333333"
        );
        assertIterableEquals(expected, answer);
    }


    @Test
    void 연속값이_하나씩_WRITE_되는_경우2() {
        inputHandler.add("E 1 3");
        inputHandler.add("W 1 0x33333333");
        inputHandler.add("W 2 0x33333333");
        inputHandler.add("W 3 0x33333333");
        List<String> answer = inputHandler.flush();
        List<String> expected = List.of(
                "W 1 0x33333333",
                "W 2 0x33333333",
                "W 3 0x33333333"
        );
        assertIterableEquals(expected, answer);
    }

}
