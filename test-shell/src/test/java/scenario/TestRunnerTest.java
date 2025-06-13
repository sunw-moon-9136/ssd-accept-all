package scenario;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.manager.IManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRunnerTest {
    public static final String TEST_SCRIPT_SAMPLE = """
            1_FullWriteAndReadCompare
            2_PartialLBAWrite
            3_WriteReadAging
            4_EraseAndWriteAging""";

    @Spy
    TestRunner testRunner;

    @Mock
    IManager mockManger;

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(TestRunner.TEST_SCRIPT_TXT_FILE_NAME));
    }

    @Nested
    class ReadTestScriptFileTest {

        @Test
        void 정상적으로_read한_경우() throws IOException {
            int expected = 4;
            Files.writeString(Path.of(TestRunner.TEST_SCRIPT_TXT_FILE_NAME),
                    TEST_SCRIPT_SAMPLE);

            testRunner.readTestScriptFile();

            assertThat(testRunner.getTestScenarios().size()).isEqualTo(expected);
            assertThat(String.join("\n", testRunner.getTestScenarios()))
                    .contains(TEST_SCRIPT_SAMPLE);
        }

        @Test
        void 파일이_없는_경우() {
            testRunner.readTestScriptFile();

            assertThat(testRunner.getTestScenarios()).isEmpty();
        }
    }

    @Nested
    class ProcessTest {

        final int EXPECTED_SCRIPTS_SIZE = 4;

        @BeforeEach
        void setUp() throws IOException {
            Files.writeString(Path.of(TestRunner.TEST_SCRIPT_TXT_FILE_NAME),
                    TEST_SCRIPT_SAMPLE);
        }

        @Test
        void TestScript가_N개_있는_경우() {
            doReturn(true).when(testRunner).run(any());

            testRunner.process(mockManger);

            verify(testRunner, times(EXPECTED_SCRIPTS_SIZE)).run(any());
        }

        @Test
        void TestScript가_없는_경우() throws IOException {
            Files.deleteIfExists(Path.of(TestRunner.TEST_SCRIPT_TXT_FILE_NAME));

            testRunner.process(mockManger);

            verify(testRunner, times(0)).run(any());
        }

        @Test
        void 잘못된_TestScript가_존재하는_경우() {
            doReturn(false).when(testRunner).run(any());

            testRunner.process(mockManger);

            verify(testRunner, times(1)).run(any());
        }

        @Test
        void TestScript가_Fail된_경우() throws IOException {
            final String WRONG_TEST_SCRIPT = """
                    1_FullWriteAndReadCompare
                    WRONG_TEXT
                    3_WriteReadAging
                    4_EraseAndWriteAging""";
            Files.writeString(Path.of(TestRunner.TEST_SCRIPT_TXT_FILE_NAME),
                    WRONG_TEST_SCRIPT, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            doReturn(false).when(testRunner).run(any());

            testRunner.process(mockManger);

            verify(testRunner, times(1)).run(any());
        }
    }
}