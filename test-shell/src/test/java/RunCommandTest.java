import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class RunCommandTest {

    @Test
    void write_호출시_SSDjar_호출되는지_확인() {
        RunCommand runCommand = new RunCommand();
        doNothing().when(runCommand).runSSDCommand(any(), any(), any());

        runCommand.write("write 3 0xAAAABBBB");

        verify(runCommand).runSSDCommand("W", "3", "0xAAAABBBB");
    }
}