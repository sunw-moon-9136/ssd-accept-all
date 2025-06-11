import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullWriteAndReadCompareTest {

    @Mock
    RunCommand runCommand;

    @Mock
    Output output;

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true()  {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand, output);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        doReturn(true).when(runCommand).execute(any());
        doAnswer(invocation -> String.format("LBA %02d : %s", atomicInteger.get(), makeHex(atomicInteger.getAndIncrement() / 5))).when(output).checkResult(anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void 하나의_readCompare라도_실패한_경우_return_false()  {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand, output);
        doReturn(true).when(runCommand).execute(any());
        doReturn("LBA 00 : 0x12345678").when(output).checkResult(anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_Exception이_발생한_경우_return_false()  {
        ITestScenario testScenario = new FullWriteAndReadCompare(runCommand, output);
        doReturn(false).when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    private String makeHex(int num) {
        return String.format("0x%02d%02d%02d%02d", num, num, num, num);
    }
}