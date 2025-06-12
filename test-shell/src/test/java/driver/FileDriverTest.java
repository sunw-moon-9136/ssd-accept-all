package driver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDriverTest {

    public static final String TEST_SOURCE_FILE_NAME = "oldFileName.txt";
    public static final String TEST_TARGET_FILE_NAME = "newFileName.txt";
    public static final String TEST_TEXT = "TEST-TEXT";


    private FileDriver fileDriver;

    @BeforeEach
    void setUp() {
        fileDriver = new FileDriver();
    }

    @Nested
    class ChangeNameIfBiggerThanTest {

        @Mock
        Supplier<Path> supplier;

        @Test
        void MaxSize가_0_이하인_경우_throw() {
            assertThatThrownBy(() -> fileDriver.changeNameIfBiggerThan(0L, TEST_SOURCE_FILE_NAME, supplier))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageStartingWith("MaxFileSize is Invalid: ");
        }

        @Test
        void Supplier가_null인_경우_throw() {
            assertThatThrownBy(() -> fileDriver.changeNameIfBiggerThan(1000L, TEST_SOURCE_FILE_NAME, null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("GetTargetPath Supplier is Null");
        }

        @Test
        void 대상_파일이_존재하지_않는_경우_throw() throws IOException {
            Files.deleteIfExists(Paths.get(TEST_SOURCE_FILE_NAME));

            assertThatThrownBy(() -> fileDriver.changeNameIfBiggerThan(1000L, TEST_SOURCE_FILE_NAME, supplier))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageStartingWith("File Not Found: ");
        }

        @Test
        void 대상_파일이_MaxSize_이하인_경우_doNothing() throws IOException {
            Path srcPath = Path.of(TEST_SOURCE_FILE_NAME);
            Files.writeString(srcPath, TEST_TEXT, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            fileDriver.changeNameIfBiggerThan(1000L, TEST_SOURCE_FILE_NAME, supplier);

            assertThat(Files.exists(srcPath)).isTrue();
            verify(supplier, never()).get();
        }

        @Test
        void 대상_파일이_MaxSize_이상인_경우_move() throws IOException {
            Path srcPath = Path.of(TEST_SOURCE_FILE_NAME);
            Files.writeString(srcPath, TEST_TEXT, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Path targetPath = Path.of(TEST_TARGET_FILE_NAME);
            doReturn(targetPath).when(supplier).get();

            fileDriver.changeNameIfBiggerThan(1L, TEST_SOURCE_FILE_NAME, supplier);

            assertThat(Files.exists(srcPath)).isFalse();
            assertThat(Files.exists(targetPath)).isTrue();
            verify(supplier, only()).get();
        }
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_SOURCE_FILE_NAME));
        Files.deleteIfExists(Paths.get(TEST_TARGET_FILE_NAME));
    }
}