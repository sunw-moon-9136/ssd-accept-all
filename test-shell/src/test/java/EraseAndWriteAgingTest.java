import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EraseAndWriteAgingTest {

    ITestScenario testScenario;

    @Mock
    RunCommand runCommand;

    @Mock
    Output output;

    @Mock
    RandomFactory randomFactory;

    @BeforeEach
    void setUp() {
        testScenario = new EraseAndWriteAging(runCommand, output, randomFactory);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        doReturn("0x99999999").when(randomFactory).getRandomHexValue();
        doReturn(true).when(runCommand).execute(any());
        doReturn("LBA XX : 0x00000000").when(output).checkResult(anyString(), anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void value가_달라서_readCompare_실패한_경우_return_false() {
        doReturn(true).when(runCommand).execute(any());
        doReturn("LBA XX : 0x12345678").when(output).checkResult(anyString(), anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_false로_리턴한_경우_return_false() {
        doReturn(false).when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}