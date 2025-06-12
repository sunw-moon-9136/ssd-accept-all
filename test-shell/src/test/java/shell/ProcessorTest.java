package shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Spy
    @InjectMocks
    Processor processor;

    @Test
    void write_SSDjar파일없을때_IOException_발생() {
        doReturn(false).when(processor).runSSDCommand(any(), any(), any());

        boolean result = processor.execute("write 3 0xAAAABBBB");

        assertFalse(result);
    }

    @Test
    void write_호출시_SSDjar_실행되어_true반환_확인() {
        doReturn(true).when(processor).runSSDCommand(any(), any(), any());

        boolean result = processor.execute("write 3 0xAAAABBBB");

        assertTrue(result);
        verify(processor).runSSDCommand("W", "3", "0xAAAABBBB");
    }

    @Test
    void write_호출시_SSDjar_실행안되어_false반환_확인() {
        doReturn(false).when(processor).runSSDCommand(any(), any(), any());

        boolean result = processor.execute("write 3 0xAAAABBBB");


        assertFalse(result);
    }

    @Test
    void read_호출시_SSDjar_실행되어_true반환_확인() {
        doReturn(true).when(processor).runSSDCommand(any(), any());

        boolean result = processor.execute("read 3");

        assertTrue(result);
        verify(processor).runSSDCommand("R", "3");
    }

    @Test
    void 존재하지_않는_명령어_입력시_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> processor.execute("abnormal 1 0xABCDFFFF"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
