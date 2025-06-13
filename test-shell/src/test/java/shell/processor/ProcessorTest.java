package shell.processor;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Spy
    @InjectMocks
    Processor processor;

    @Nested
    class WriteCommandTests {
        @Test
        void write_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("write 3 0xAAAABBBB");

            assertFalse(result);
        }

        @Test
        void write_정상처리되어_true반환_확인() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("write 3 0xAAAABBBB");

            assertTrue(result);
            verify(processor).runSSDCommand("W", "3", "0xAAAABBBB");
        }
    }

    @Nested
    class ReadCommandTests {
        @Test
        void read_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any(), any());

            boolean result = processor.execute("read 3");

            assertFalse(result);
        }

        @Test
        void write_정상처리되어_true반환_확인() {
            doReturn(true).when(processor).runSSDCommand(any(), any());

            boolean result = processor.execute("read 3");

            assertTrue(result);
            verify(processor).runSSDCommand("R", "3");
        }
    }

    @Nested
    class EraseCommandTests {
        @Test
        void erase_size양수_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 0 10");

            assertFalse(result);
        }

        @Test
        void erase_size음수_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 0 -100");

            assertFalse(result);
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인1() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 0 10");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "0", "10");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인2() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 2 15");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "2", "10");
            verify(processor).runSSDCommand("E", "12", "5");
            verify(processor, times(2)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인3() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 99 15");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "99", "1");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인1() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 0 -1");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "0", "1");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인2() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 5 -2");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "4", "2");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인3() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase 40 -15");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "26", "10");
            verify(processor).runSSDCommand("E", "36", "5");
            verify(processor, times(2)).runSSDCommand(any(), any(), any());
        }
    }
    @Nested
    class EraseRangeCommandTests {
        @Test
        void erase_range_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase_range 0 10");

            assertFalse(result);
        }

        @Test
        void erase_range_정상처리되어_true반환_확인1() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase_range 0 5");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "0", "6");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_range_정상처리되어_true반환_확인2() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase_range 5 0");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "0", "6");
            verify(processor, times(1)).runSSDCommand(any(), any(), any());
        }

        @Test
        void erase_range_정상처리되어_true반환_확인3() {
            doReturn(true).when(processor).runSSDCommand(any(), any(), any());

            boolean result = processor.execute("erase_range 0 20");

            assertTrue(result);
            verify(processor).runSSDCommand("E", "0", "10");
            verify(processor).runSSDCommand("E", "10", "10");
            verify(processor).runSSDCommand("E", "20", "1");
            verify(processor, times(3)).runSSDCommand(any(), any(), any());
        }

    }

    @Nested
    class FlushCommandTests {
        @Test
        void flush_IOException발생시_false반환_발생확인() {
            doReturn(false).when(processor).runSSDCommand(any());

            boolean result = processor.execute("flush");

            assertFalse(result);
        }

        @Test
        void flush_정상처리되어_true반환_확인() {
            doReturn(true).when(processor).runSSDCommand(any());

            boolean result = processor.execute("flush");

            assertTrue(result);
            verify(processor).runSSDCommand("F");
        }
    }

    @Test
    void 존재하지_않는_명령어_입력시_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> processor.execute("abnormal 1 0xABCDFFFF"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
