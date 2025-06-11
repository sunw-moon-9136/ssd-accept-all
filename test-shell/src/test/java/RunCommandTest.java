import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunCommandTest {
    @Spy
    @InjectMocks
    RunCommand runCommand;

    @Test
    void write_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        doReturn(false).when(runCommand).runSSDCommand(any(), any(), any());

        boolean result = runCommand.execute("write 3 0xAAAABBBB");

        assertFalse(result);
    }

    @Test
    void write_호출시_SSDjar_실행되어_true반환_확인() throws Exception {
        doReturn(true).when(runCommand).runSSDCommand(any(), any(), any());

        boolean result = runCommand.execute("write 3 0xAAAABBBB");

        assertTrue(result);
        verify(runCommand).runSSDCommand("W", "3", "0xAAAABBBB");
    }

    @Test
    void write_호출시_SSDjar_실행안되어_false반환_확인() throws IOException, InterruptedException {
        doReturn(false).when(runCommand).runSSDCommand(any(), any(), any());

        boolean result = runCommand.execute("write 3 0xAAAABBBB");

        assertFalse(result);
    }

    @Test
    void read_호출시_SSDjar_실행되어_true반환_확인() throws Exception {
        doReturn(true).when(runCommand).runSSDCommand(any(), any());

        boolean result = runCommand.execute("read 3");

        assertTrue(result);
        verify(runCommand).runSSDCommand("R", "3");
    }

    @Test
    void 존재하지_않는_명령어_입력시_IllegalArgumentException_발생() throws Exception {
        assertThatThrownBy(() -> runCommand.execute("abnormal 1 0xABCDFFFF"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}