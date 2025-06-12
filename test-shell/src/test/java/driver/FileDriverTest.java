package driver;

import logger.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDriverTest {

    public static final String TEST_SOURCE_FILE_NAME = "oldFileName.log";
    public static final String TEST_TARGET_FILE_NAME = "newFileName.log";
    public static final String TEST_TEXT = "TEST-TEXT";

    @Nested
    class ChangeNameIfBiggerThanTest {

        private FileDriver fileDriver;

        @BeforeEach
        void setUp() {
            fileDriver = new FileDriver();
        }

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
            Files.deleteIfExists(Path.of(TEST_SOURCE_FILE_NAME));

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

    @Nested
    class ChangeOldLogFileNameTest {

        final String LOG_FILE_NAME_260306 = "until_260306_00h_00m_00s.log";
        final String LOG_FILE_NAME_260307 = "until_260307_00h_00m_00s.log";
        final String LOG_FILE_NAME_260308 = "until_260308_00h_00m_00s.log";
        final String LOG_FILE_NAME_260309 = "until_260309_00h_00m_00s.log";
        final String LOG_FILE_NAME_260310 = "until_260310_00h_00m_00s.log";

        @Spy
        private FileDriver fileDriver;

        @Test
        void Log파일이_없는_경우_doNothing() {
            fileDriver.changeOldLogFileName(Logger.LATEST_LOG_FILE_NAME);

            verify(fileDriver, times(0)).convertToZipFile(anyString());
        }

        @Test
        void Log파일이_latest_log만_있는_경우_doNothing() throws IOException {
            Files.writeString(Path.of(Logger.LATEST_LOG_FILE_NAME), TEST_TEXT, StandardOpenOption.CREATE);

            fileDriver.changeOldLogFileName(Logger.LATEST_LOG_FILE_NAME);

            verify(fileDriver, times(0)).convertToZipFile(anyString());
        }

        @Test
        void Log파일이_latest_log를_포함해_2개만_있는_경우_doNothing() throws IOException {
            Files.writeString(Path.of(Logger.LATEST_LOG_FILE_NAME), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260306), TEST_TEXT, StandardOpenOption.CREATE);

            fileDriver.changeOldLogFileName(Logger.LATEST_LOG_FILE_NAME);

            verify(fileDriver, times(0)).convertToZipFile(anyString());
        }

        @Test
        void Log파일이_latest_log를_포함해_2개보다_많이_있는_경우_2개_Log파일_빼고_zip파일로_변경() throws IOException {
            Files.writeString(Path.of(Logger.LATEST_LOG_FILE_NAME), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260306), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260307), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260308), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260309), TEST_TEXT, StandardOpenOption.CREATE);
            Files.writeString(Path.of(LOG_FILE_NAME_260310), TEST_TEXT, StandardOpenOption.CREATE);

            fileDriver.changeOldLogFileName(Logger.LATEST_LOG_FILE_NAME);

            verify(fileDriver, times(4)).convertToZipFile(anyString());
        }

        @AfterEach
        void cleanUp() throws IOException {
            try (Stream<Path> walk =  Files.walk(Path.of("."))) {
                walk.filter(Files::isRegularFile) // 일반 파일만 선택
                        .map(p -> p.getFileName().toString())
                        .filter(p -> p.endsWith(".log") || p.endsWith(".zip"))
                        .forEach(name -> {
                            try {
                                Files.deleteIfExists(Path.of(name));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_SOURCE_FILE_NAME));
        Files.deleteIfExists(Path.of(TEST_TARGET_FILE_NAME));
    }
}