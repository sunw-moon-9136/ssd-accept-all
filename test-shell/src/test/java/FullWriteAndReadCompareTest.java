import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class FullWriteAndReadCompareTest {

    @Mock
    RunCommand runCommand;

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() throws IOException, InterruptedException {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand);
        doNothing().when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void 하나의_readCompare라도_실패한_경우_return_false() throws IOException, InterruptedException {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand);
        doNothing().when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_Exception이_발생한_경우_return_false() throws IOException, InterruptedException {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand);
        doThrow(new IOException()).when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}