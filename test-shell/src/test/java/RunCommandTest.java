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
    @Mock
    Output mockOutput;

    @Spy
    @InjectMocks  // 💡 이거 붙이면 mockOutput이 생성자에 주입됨
    RunCommand runCommand;

    @Test
    void write_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        doThrow(new IOException("테스트")).when(runCommand).runSSDCommand(any(), any(), any());

        assertThatThrownBy(() -> runCommand.execute("write 3 0xAAAABBBB"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("테스트");
    }

    @Test
    void write_호출시_SSDjar_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("write 3 0xAAAABBBB");

        verify(runCommand).runSSDCommand("W", "3", "0xAAAABBBB");
    }

    @Test
    void write_호출시_Output_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("write 3 0xAAAABBBB");

        verify(mockOutput).checkResult("write");
    }

    @Test
    void read_SSDjar파일없을때_IOException_발생() throws IOException, InterruptedException {
        doThrow(new IOException("테스트")).when(runCommand).runSSDCommand(any(), any());

        assertThatThrownBy(() -> runCommand.execute("read 3"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("테스트");
    }

    @Test
    void read_호출시_SSDjar_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("read 3");

        verify(runCommand).runSSDCommand("R", "3");
    }

    @Test
    void read_호출시_Output_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("read 3");

        verify(mockOutput).checkResult("read");
    }

    @Test
    void fullread_호출시_모든_LBA에_read호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("fullread");

        for (int i = 0; i < 100; i++) {
            verify(runCommand).runSSDCommand("R", String.valueOf(i));
        }
    }

    @Test
    void fullread_호출시_write_100번_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any());

        runCommand.execute("fullread");

        verify(runCommand, times(100)).runSSDCommand(eq("R"), any());
        verify(mockOutput, times(100)).checkResult("read");
    }

    @Test
    void fullwrite_호출시_모든_LBA에_write호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("fullwrite 0xABCDFFFF");

        for (int i = 0; i < 100; i++) {
            verify(runCommand).runSSDCommand("W", String.valueOf(i),"0xABCDFFFF");
        }
    }

    @Test
    void fullwrite_호출시_write_100번_호출되는지_확인() throws Exception {
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.execute("fullwrite 0xABCDFFFF");

        verify(runCommand, times(100)).runSSDCommand(eq("W"), any(), eq("0xABCDFFFF"));
        verify(mockOutput, times(100)).checkResult("write");
    }

    @Test
    void 존재하지_않는_명령어_입력시_IllegalArgumentException_발생() throws Exception {
        RunCommand runCommand = new RunCommand(mockOutput);

        assertThatThrownBy(() -> runCommand.execute("abnormal 1 0xABCDFFFF"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}