import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

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
        testScenario = new WriteReadAging(runCommand, output);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        String randomValue = "0x00000000";
        doReturn(randomValue).when(randomFactory).getRandomHexValue();
        doReturn(true).when(runCommand).execute(any());
        doAnswer(invocation -> {
            if(atomicBoolean.get())
                return "LBA 00 : " + randomValue;
            atomicBoolean.set(false);
            return "LBA 99 : " + randomValue;
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