package scenario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.manager.IManager;
import utils.RandomFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EraseAndWriteAgingTest {

    ITestScenario testScenario;

    @Mock
    IManager manager;

    @Mock
    RandomFactory randomFactory;

    @BeforeEach
    void setUp() {
        testScenario = EraseAndWriteAging.getInstance(manager, randomFactory);
    }

    @Test
    void 정상적으로_모든_readCompare가_성공한_경우_return_true() {
        doReturn(true).when(manager).erase_range(anyInt(), anyInt());
        doReturn("0x99999999").when(randomFactory).getRandomHexValue();
        doReturn(true).when(manager).write(anyInt(), any());
        doReturn("LBA XX : 0x00000000").when(manager).read(anyInt());

        boolean actual = testScenario.run();

        assertThat(actual).isTrue();
    }

    @Test
    void value가_달라서_readCompare_실패한_경우_return_false() {
        doReturn(true).when(manager).erase_range(anyInt(), anyInt());
        doReturn(true).when(manager).write(anyInt(), any());
        doReturn("LBA XX : 0x12345678").when(manager).read(anyInt());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void write에서_false로_리턴한_경우_return_false() {
        doReturn(true).when(manager).erase_range(anyInt(), anyInt());
        doReturn(false).when(manager).write(anyInt(), any());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }

    @Test
    void erase_range에서_false로_리턴한_경우_return_false() {
        doReturn(false).when(manager).erase_range(anyInt(), anyInt());

        boolean actual = testScenario.run();

        assertThat(actual).isFalse();
    }
}