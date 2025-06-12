package scenario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.processor.Processor;
import shell.output.Output;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartialLBAWriteTest {

    @Mock
    Processor processor;

    @Mock
    Output output;

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        ITestScenario testScenario = new PartialLBAWrite(processor, output);
        doReturn(true).when(processor).execute(any());

        String[] inputAddressList = {"4", "0", "3", "1", "2"};
        AtomicInteger idx = new AtomicInteger(0);

        doAnswer(invocationOnMock -> {
            int index = idx.getAndIncrement() % inputAddressList.length;
            int value = Integer.parseInt(inputAddressList[index]);
            return String.format("LBA %02d : 0xAAAABBBB", value);
        }).when(output).checkResult(anyString(), anyString());

        boolean actual = testScenario.run();
        assertThat(actual).isTrue();
    }

    @Test
    void 하나의_readCompare라도_실패한_경우_return_false() {
        ITestScenario testScenario = new PartialLBAWrite(processor, output);
        doReturn(true).when(processor).execute(any());
        boolean actual = testScenario.run();
        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_Exception이_발생한_경우_return_false() {
        ITestScenario testScenario = new PartialLBAWrite(processor, output);
        doReturn(false).when(processor).execute(any());
        boolean actual = testScenario.run();
        assertThat(actual).isFalse();
    }
}
