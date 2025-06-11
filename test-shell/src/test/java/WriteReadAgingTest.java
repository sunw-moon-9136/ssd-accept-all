import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WriteReadAgingTest {

    ITestScenario testScenario;

    @Mock
    RunCommand runCommand;

    @Mock
    Output output;

    @Mock
    RandomFactory randomFactory;

    @BeforeEach
    void setUp() {
        testScenario = new WriteReadAging(runCommand, output, randomFactory);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        AtomicBoolean isAddress00 = new AtomicBoolean(false);
        String testValue = "0x00000000";
        doReturn(testValue).when(randomFactory).getRandomHexValue();
        doReturn(true).when(runCommand).execute(any());
        doAnswer(invocation -> {
            isAddress00.set(!isAddress00.get());
            if (isAddress00.get())
                return "LBA 00 : " + testValue;
            return "LBA 99 : " + testValue;
        }).when(output).checkResult(anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void value가_달라서_readCompare_실패한_경우_return_false() {
        doReturn("0x12488321").when(randomFactory).getRandomHexValue();
        doReturn(true).when(runCommand).execute(any());
        doReturn("LBA 00 : 0x12345678").when(output).checkResult(anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void address가_달라서_readCompare_실패한_경우_return_false() {
        doReturn("0x00000000").when(randomFactory).getRandomHexValue();
        doReturn(true).when(runCommand).execute(any());
        doReturn("LBA 50 : 0x00000000").when(output).checkResult(anyString());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void runCommand에서_execute가_false로_나온_경우_return_false() {
        doReturn(false).when(runCommand).execute(any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}