package shell.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.Processor;
import shell.output.Output;
import utils.LogFileDeleter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerTest {
    public static final String INVALID_COMMAND = "INVALID COMMAND";

    @Mock
    Output mockOutput;

    @Mock
    Processor mockProcessor;

    Manager manager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        manager = new Manager(mockProcessor, mockOutput);
    }

    @AfterEach
    void cleanUp() throws IOException {
        LogFileDeleter.deleteRecursivelyLogDirectory();
    }

    @Nested
    class CommandInputTest {

        @Mock
        Manager mockManager;

        @Test
        void read_Command_입력시_value_반환() {
            doReturn("0xAAAAAAAA").when(mockManager).read(anyInt());

            String expect = "0xAAAAAAAA";
            String actual = mockManager.read(3);

            assertEquals(expect, actual);
        }

        @Test
        void write_Command_입력시_true_반환() {
            doReturn(true).when(mockManager).write(anyInt(), anyString());

            boolean actual = mockManager.write(3, "0xAAAAFFFF");

            assertThat(actual).isTrue();
        }

    }

    @Nested
    class outputTest {
        @Test
        void read_정상입력시_output_checkResult_호출확인() {
            doReturn(true).when(mockProcessor).execute("read 3");
            doReturn("0xABCDFFFF").when(mockOutput).checkResult("read 3");

            String actual = manager.read(3);

            assertEquals("0xABCDFFFF", actual);
        }

        @Test
        void write_정상입력시_output_checkResult_호출확인() {
            doReturn(true).when(mockProcessor).execute("write 3 0xABCDFFFF");
            doReturn("DONE").when(mockOutput).checkResult("write 3 0xABCDFFFF");

            manager.write(3, "0xABCDFFFF");

            verify(mockOutput).checkResult("write 3 0xABCDFFFF");
        }
    }
}