import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RunCommandTest {

    @Test
    void write_호출시_SSDjar_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand());
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.write("write 3 0xAAAABBBB");

        verify(runCommand).runSSDCommand("W", "3", "0xAAAABBBB");
    }

    @Test
    void write_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        RunCommand runCommand = spy(new RunCommand());
        doThrow(new IOException("테스트")).when(runCommand).runSSDCommand(any(), any(), any());

        assertThatThrownBy(() -> runCommand.write("write 3 0xAAAABBBB"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("테스트");
    }

    @Test
    void read_호출시_SSDjar_호출되는지_확인() throws Exception {
        RunCommand runCommand = spy(new RunCommand());
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.read("read 3");

        verify(runCommand).runSSDCommand("R", "3");
    }
}