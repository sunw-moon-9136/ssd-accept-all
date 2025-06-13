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

class BufferOptimizerTest {

    public SsdCommandBufferOptimizer ssdCommandBufferOptimizer;

    String BUFFER_PATH = "buffer";

    @BeforeEach
    void setUp() {
        ssdCommandBufferOptimizer = new SsdCommandBufferOptimizer();
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
        ssdCommandBufferOptimizer.add("E 0 6");
        ssdCommandBufferOptimizer.add("W 3 0xABCDABCD");
        ssdCommandBufferOptimizer.add("E 0 4");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "E 0 6"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void test1() {
        ssdCommandBufferOptimizer.add("W 33 0x33333333");
        ssdCommandBufferOptimizer.add("E 43 10");
        ssdCommandBufferOptimizer.add("W 44 0x33333333");
        ssdCommandBufferOptimizer.add("W 33 0x44444444");

        List<String> answer = ssdCommandBufferOptimizer.flush();
    }


    @Test
    void IGNORE_COMMAND1() {
        ssdCommandBufferOptimizer.add("W 20 0xABCDABCD");
        ssdCommandBufferOptimizer.add("W 21 0x12341234");
        ssdCommandBufferOptimizer.add("W 20 0xEEEEFFFF");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "W 21 0x12341234",
                "W 20 0xEEEEFFFF"
        );
        assertIterableEquals(expected, answer);
    }


    @Test
    void IGNORE_COMMAND2() {
        ssdCommandBufferOptimizer.add("E 18 3");
        ssdCommandBufferOptimizer.add("W 21 0x12341234");
        ssdCommandBufferOptimizer.add("E 18 5");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "E 18 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void MERGE_ERASE_TEST() {
        ssdCommandBufferOptimizer.add("W 20 0xABCDABCD");
        ssdCommandBufferOptimizer.add("E 10 4");
        ssdCommandBufferOptimizer.add("E 12 3");

        List<String> answer = ssdCommandBufferOptimizer.flush();

        List<String> expected = List.of(
                "W 20 0xABCDABCD",
                "E 10 5"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void FAST_READ_TEST() {
        ssdCommandBufferOptimizer.add("W 10 0xABCDABCD");
        ssdCommandBufferOptimizer.add("E 10 4");
        ssdCommandBufferOptimizer.add("W 11 0xABCDABCD");
        assertThat(ssdCommandBufferOptimizer.read(10)).isEqualTo("0x00000000");
        assertThat(ssdCommandBufferOptimizer.read(11)).isEqualTo("0xABCDABCD");
    }

    @Test
    void BUFFER에_없는_값_FAST_READ_TEST() {
        assertThat(ssdCommandBufferOptimizer.read(10)).isEqualTo("");
        assertThat(ssdCommandBufferOptimizer.read(11)).isEqualTo("");
    }

    @Test
    void ERASE_SIZE_0일때_버퍼_미생성() {
        ssdCommandBufferOptimizer.add("E 10 0");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        assertTrue(answer.isEmpty());
    }

    @Test
    void ERASE_SIZE_1이_존재할때_동일주소_WRITE() {
        ssdCommandBufferOptimizer.add("E 10 1");
        ssdCommandBufferOptimizer.add("W 10 0xABCDABCD");
        List<String> answer = ssdCommandBufferOptimizer.flush();
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
        ssdCommandBufferOptimizer.add("E 10 5");
        ssdCommandBufferOptimizer.add("W 10 0xABCDABCD");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "W 10 0xABCDABCD",
                "E 11 4"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void WRITE가_ERASE_END_부분이면_ERASE범위_조절() {
        ssdCommandBufferOptimizer.add("E 10 5");
        ssdCommandBufferOptimizer.add("W 14 0xABCDABCD");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "W 14 0xABCDABCD",
                "E 10 4"
        );
        assertIterableEquals(expected, answer);
    }

    @Test
    void ERASE_중복_범위_삽입() {
        ssdCommandBufferOptimizer.add("E 0 5");
        ssdCommandBufferOptimizer.add("E 2 1");
        List<String> answer = ssdCommandBufferOptimizer.flush();
        List<String> expected = List.of(
                "E 0 5"
        );
        assertIterableEquals(expected, answer);
    }

}