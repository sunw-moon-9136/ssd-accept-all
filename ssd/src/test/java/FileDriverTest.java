import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDriverTest {

    public static final byte[] TEST_BYTES = {1, 2, 3, 4};

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
}