package scenario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.Processor;
import shell.output.Output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullWriteAndReadCompareTest {

    ITestScenario testScenario;

    @Mock
    Processor processor;

    @Mock
    Output output;

    @Mock
    RandomFactory randomFactory;

    @BeforeEach
    void setUp() {
        testScenario = new FullWriteAndReadCompare(processor, output, randomFactory);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        doReturn("0x12345678").when(randomFactory).getRandomHexValue();
        doReturn(true).when(processor).execute(any());
        doReturn("LBA XX : 0x12345678").when(output).checkResult(anyString(), anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void value가_달라서_readCompare_실패한_경우_return_false() {
        doReturn(true).when(processor).execute(any());
        doReturn("LBA 00 : 0x12345678").when(output).checkResult(anyString(), anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_Exception이_발생한_경우_return_false() {
        doReturn(false).when(processor).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}
