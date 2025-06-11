import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunCommandTest {
    @Mock
    Output mockOutput;

    @Test
    void write_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doThrow(new IOException("테스트")).when(runCommand).runSSDCommand(any(), any(), any());

        assertThatThrownBy(() -> runCommand.execute("write 3 0xAAAABBBB"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("테스트");
    }

    @Test
    void write_호출시_SSDjar_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("write 3 0xAAAABBBB");

        verify(runCommand).runSSDCommand("W", "3", "0xAAAABBBB");
    }

    @Test
    void write_호출시_Output_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("write 3 0xAAAABBBB");

        verify(mockOutput).run("write");
    }

    @Test
    void read_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doThrow(new IOException("테스트")).when(runCommand).runSSDCommand(any(), any());

        assertThatThrownBy(() -> runCommand.execute("read 3"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("테스트");
    }

    @Test
    void read_호출시_SSDjar_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("read 3");

        verify(runCommand).runSSDCommand("R", "3");
    }

    @Test
    void read_호출시_Output_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("read 3");

        verify(mockOutput).run("read");
    }

    @Test
    void fullwrite_호출시_모든_LBA에_write호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand(mockOutput));
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("fullwrite 0xABCDFFFF");

        for (int i = 0; i < 100; i++) {
            verify(runCommand).runSSDCommand("W", String.valueOf(i), "0xABCDFFFF");
        }
        verify(mockOutput).run("write");
    }

}