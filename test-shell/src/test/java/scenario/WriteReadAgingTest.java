package scenario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.manager.IManager;
import utils.RandomFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WriteReadAgingTest {

    ITestScenario testScenario;

    @Mock
    IManager manager;

    @Mock
    RandomFactory randomFactory;

    @BeforeEach
    void setUp() {
        testScenario = new WriteReadAging(manager, randomFactory);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        AtomicBoolean isAddress00 = new AtomicBoolean(false);
        String testValue = "0x00000000";
        doReturn(testValue).when(randomFactory).getRandomHexValue();
        doReturn(true).when(manager).write(anyInt(), any());
        doAnswer(invocation -> {
            isAddress00.set(!isAddress00.get());
            if (isAddress00.get())
                return "LBA 00 : " + testValue;
            return "LBA 99 : " + testValue;
        }).when(manager).read(anyInt());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void value가_달라서_readCompare_실패한_경우_return_false() {
        doReturn("0x12488321").when(randomFactory).getRandomHexValue();
        doReturn(true).when(manager).write(anyInt(), any());
        doReturn("LBA 00 : 0x12345678").when(manager).read(anyInt());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void write에서_false로_리턴한_경우_return_false() {
        doReturn(false).when(manager).write(anyInt(), any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}