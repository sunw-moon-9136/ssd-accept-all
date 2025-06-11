import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutputTest {


    final static String OUTPUT_TEXT_READ_PASS = "1 0xAAAABBBB";
    final static String OUTPUT_TEXT_ERROR = "ERROR";
    final static String OUTPUT_TEXT_WRITE_PASS = null;

    final static String RESULT_STRING_READ_PASS = "[read] " + OUTPUT_TEXT_READ_PASS;
    final static String RESULT_STRING_READ_ERROR = "[read] " + OUTPUT_TEXT_ERROR;
    final static String RESULT_STRING_WRITE_PASS = "[write] DONE";
    final static String RESULT_STRING_WRITE_ERROR = "[write] ERROR";


    @Mock
    private DataReader mockDataReader;
    private Output output;

    @BeforeEach
    void setUp() {
        output = new Output(mockDataReader);
    }

    @Test
    void output_파일이_있으면_true를_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        boolean actual = output.existFileCheck();
        assertEquals(true, actual);
    }

    @Test
    void output_파일이_없으면_false를_반환한다() {
        when(mockDataReader.exists()).thenReturn(false);
        boolean actual = output.existFileCheck();
        assertEquals(false, actual);
    }

    @Test
    void 받은명령어가_READ이고_파일이_있으면_파일내용을_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_READ_PASS);
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_PASS, result);
        verify(mockDataReader, times(1)).readLine();
    }

    @Test
    void 받은명령어가_WRITE이고_파일내용이_비어있으면_DONE을_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_WRITE_PASS);
        String result = output.checkResult("write");
        assertEquals(RESULT_STRING_WRITE_PASS, result);
    }

    @Test
    void 받은명령어가_WRITE이고_파일내용이_있으면_FAIL을_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_ERROR);
        String result = output.checkResult("write");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
    }


    @Test
    void READ명령을_3번받으면_readLine도_3번_호출된다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_READ_PASS);
        output.checkResult("read");
        output.checkResult("read");
        output.checkResult("read");
        verify(mockDataReader, times(3)).readLine();
    }
}


class ActualTest {
    private Output output;

    @BeforeEach
    void setUp() {
        output = ReadOutputFactory.readOutput();
    }


    @Test
    void output_파일이_있으면_PASS() throws IOException {

        boolean expected = true;
        boolean act = output.existFileCheck();
        assertEquals(expected, act);
    }

    @Nested
    @DisplayName("명령어가 'read'일 때")
    @Disabled
    class ReadTest {
        @Test
        void 받은명령어가_READ이면_OUTPUT파일을_읽는다() throws IOException {

            boolean expected = true;
            String act = output.checkResult("read");
            assertNotNull(act);

        }
    }

    @Nested
    @DisplayName("명령어가 'Write'일 때")
    class WriteTest {
        @Disabled
        @Test
        void 받은명령어가_write일때_정상동작_확인() throws IOException {

            boolean expected = true;
            String act = output.checkResult("write");
            assertEquals("[write] DONE", act);
        }

        @Test
        void 받은명령어가_write일때_fail확인() throws IOException {

            boolean expected = true;
            String act = output.checkResult("write");
            assertEquals("FAIL", act);
        }
    }
}
