import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDriverTest {

    public static final byte[] TEST_BYTES = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private FileDriver fileDriver;

    @BeforeEach
    void setUp() {
        fileDriver = new FileDriver();
    }

    @Nested
    class FileNameExceptionTest {

        public static final String INVALID_FILE_NAME = "no_file.txt";

        @Test
        void 파일명을_null으로_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_empty_string_으로_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_null으로_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write(null, TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_empty_string_으로_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write("", TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 잘못된_종류의_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(INVALID_FILE_NAME))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 잘못된_종류의_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write(INVALID_FILE_NAME, TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class WriteTest {

        public static final String NOT_IMPORTANT_ALREADY_EXIST_OUTPUT_TEXT = "Bye Bye, World!";

        @Test
        void 대상_파일이_존재하지_않는_경우_파일_새로_생성하여_write() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.deleteIfExists(path);

            fileDriver.write(FileDriver.NAND_FILE_NAME, TEST_BYTES);

            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.readString(path)).isEqualTo(new String(TEST_BYTES, StandardCharsets.UTF_8));
        }

        @Test
        void 대상_파일이_존재하는_경우_파일_덮어쓰기하여_write() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.writeString(path, NOT_IMPORTANT_ALREADY_EXIST_OUTPUT_TEXT,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            fileDriver.write(FileDriver.NAND_FILE_NAME, TEST_BYTES);

            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.readString(path)).isEqualTo(new String(TEST_BYTES, StandardCharsets.UTF_8));
        }
    }
}