package scenario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.manager.IManager;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartialLBAWriteTest {

    ITestScenario testScenario;

    @Mock
    IManager manager;

    @BeforeEach
    void test() {
        testScenario = new PartialLBAWrite(manager);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        doReturn(true).when(manager).write(anyInt(), any());
        String[] inputAddressList = {"4", "0", "3", "1", "2"};
        AtomicInteger idx = new AtomicInteger(0);
        doAnswer(invocationOnMock -> {
            int index = idx.getAndIncrement() % inputAddressList.length;
            int value = Integer.parseInt(inputAddressList[index]);
            return String.format("LBA %02d : 0xAAAABBBB", value);
        }).when(manager).read(anyInt());

        boolean actual = testScenario.run();
        assertThat(actual).isTrue();
    }

    @Test
    void 하나의_readCompare라도_실패한_경우_return_false() {
        doReturn(true).when(manager).write(anyInt(), any());
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
