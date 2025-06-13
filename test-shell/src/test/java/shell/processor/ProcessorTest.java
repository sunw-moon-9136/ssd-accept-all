package shell.processor;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.processor.command.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Mock
    SSDRunner mockRunner;

    @Nested
    class WriteCommandTests {
        @Test
        void write_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any(), any(), any());
            WriteCommand command = new WriteCommand(mockRunner);

            boolean result = command.execute(new String[]{"3", "0xAAAABBBB"});

            assertFalse(result);
            verify(mockRunner).run("W", "3", "0xAAAABBBB");
        }

        @Test
        void write_정상처리되어_true반환_SSDRunner호출확인() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            WriteCommand command = new WriteCommand(mockRunner);

            boolean result = command.execute(new String[]{"3", "0xAAAABBBB"});

            assertTrue(result);
            verify(mockRunner).run("W", "3", "0xAAAABBBB");
        }
    }

    @Nested
    class ReadCommandTests {
        @Test
        void read_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any(), any());
            ReadCommand command = new ReadCommand(mockRunner);

            boolean result = command.execute(new String[]{"3"});

            assertFalse(result);
            verify(mockRunner).run("R", "3");
        }

        @Test
        void read_정상처리되어_true반환_SSDRunner호출확인() {
            doReturn(true).when(mockRunner).run(any(), any());
            ReadCommand command = new ReadCommand(mockRunner);

            boolean result = command.execute(new String[]{"3"});

            assertTrue(result);
            verify(mockRunner).run("R", "3");
        }
    }

    @Nested
    class EraseCommandTests {
        @Test
        void erase_size양수_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "10"});

            assertFalse(result);
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인1() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "10"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "10");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인2() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"2", "15"});

            assertTrue(result);
            verify(mockRunner).run("E", "2", "10");
            verify(mockRunner).run("E", "12", "5");
            verify(mockRunner, times(2)).run(any(), any(), any());
        }

        @Test
        void erase_size양수_정상처리되어_true반환_확인3() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"99", "15"});

            assertTrue(result);
            verify(mockRunner).run("E", "99", "1");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_size음수_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "-100"});

            assertFalse(result);
            verify(mockRunner).run("E", "0", "1");
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인1() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "-1"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "1");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인2() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"5", "-2"});

            assertTrue(result);
            verify(mockRunner).run("E", "4", "2");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_size음수_정상처리되어_true반환_확인3() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseCommand command = new EraseCommand(mockRunner);

            boolean result = command.execute(new String[]{"40", "-15"});

            assertTrue(result);
            verify(mockRunner).run("E", "26", "10");
            verify(mockRunner).run("E", "36", "5");
            verify(mockRunner, times(2)).run(any(), any(), any());
        }
    }

    @Nested
    class EraseRangeCommandTests {
        @Test
        void erase_range_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any(), any(), any());
            EraseRangeCommand command = new EraseRangeCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "10"});

            assertFalse(result);
            verify(mockRunner).run("E", "0", "10");
        }

        @Test
        void erase_range_정상처리되어_true반환_확인1() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseRangeCommand command = new EraseRangeCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "5"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "6");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_range_정상처리되어_true반환_확인2() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseRangeCommand command = new EraseRangeCommand(mockRunner);

            boolean result = command.execute(new String[]{"5", "0"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "6");
            verify(mockRunner, times(1)).run(any(), any(), any());
        }

        @Test
        void erase_range_정상처리되어_true반환_확인3() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseRangeCommand command = new EraseRangeCommand(mockRunner);

            boolean result = command.execute(new String[]{"0", "20"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "10");
            verify(mockRunner).run("E", "10", "10");
            verify(mockRunner).run("E", "20", "1");
            verify(mockRunner, times(3)).run(any(), any(), any());
        }

        @Test
        void erase_range_정상처리되어_true반환_확인4() {
            doReturn(true).when(mockRunner).run(any(), any(), any());
            EraseRangeCommand command = new EraseRangeCommand(mockRunner);

            boolean result = command.execute(new String[]{"20", "0"});

            assertTrue(result);
            verify(mockRunner).run("E", "0", "10");
            verify(mockRunner).run("E", "10", "10");
            verify(mockRunner).run("E", "20", "1");
            verify(mockRunner, times(3)).run(any(), any(), any());
        }
    }

    @Nested
    class FlushCommandTests {
        @Test
        void flush_IOException발생시_false반환_발생확인() {
            doReturn(false).when(mockRunner).run(any());
            FlushCommand command = new FlushCommand(mockRunner);

            boolean result = command.execute(new String[]{});

            assertFalse(result);
        }

        @Test
        void flush_정상처리되어_true반환_확인() {
            doReturn(true).when(mockRunner).run(any());
            FlushCommand command = new FlushCommand(mockRunner);

            boolean result = command.execute(new String[]{});

            assertTrue(result);
            verify(mockRunner).run("F");
            verify(mockRunner, times(1)).run(any());
        }
    }
}
