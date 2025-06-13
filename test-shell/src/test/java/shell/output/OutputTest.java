package shell.output;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OutputTest {

    final static String OUTPUT_TEXT_READ_PASS = "0xAAAABBBB";
    final static String OUTPUT_TEXT_ERROR = "ERROR";
    final static String OUTPUT_TEXT_WRITE_PASS = null;

    final static String RESULT_STRING_READ_PASS = "0xAAAABBBB";
    final static String RESULT_STRING_READ_ERROR = "ERROR";
    final static String RESULT_STRING_WRITE_PASS = "DONE";
    final static String RESULT_STRING_WRITE_ERROR = "ERROR";


    @Mock
    private DataReader mockDataReader;
    private Output output;


    @BeforeEach
    void setUp() {
        output = new Output(mockDataReader);
    }


    //outputfile 있는지 여부 체크
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

    //outputfile 있을 경우 READ TEST
    @Test
    void 받은명령어가_READ이고_파일이_있으면_파일내용을_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_READ_PASS);
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_PASS, result);
        verify(mockDataReader, times(1)).readLine();
    }

    @Test
    void 받은명령어가_READ이고_파일이_없으면_ERROR반환() {
        when(mockDataReader.exists()).thenReturn(false);
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_ERROR, result);
        verify(mockDataReader, never()).readLine();
    }

    @Test
    void 받은명령어가_READ이고_결과값이_ERROR일때() {
        when(mockDataReader.exists()).thenReturn(true);
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_ERROR, result);
        verify(mockDataReader, times(1)).readLine();
    }

    @Test
    void 받은명령어가_READ이고_exception이_발생했을때() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenThrow(new RuntimeException("Simulated file read error"));
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_ERROR, result);
        verify(mockDataReader, times(1)).readLine();
    }

    @Test
    void 받은명령어가_READ이고_결과값에_형태가_맞지않을때() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(" 11111");
        String result = output.checkResult("read");
        assertEquals(RESULT_STRING_READ_ERROR, result);
        verify(mockDataReader, times(1)).readLine();
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


    @Test
    void 받은명령어가_WRITE이고_파일내용이_비어있으면_DONE을_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_WRITE_PASS);
        String result = output.checkResult("write");
        assertEquals(RESULT_STRING_WRITE_PASS, result);
    }

    @Test
    void 받은명령어가_WRITE이고_exception이_발생했을때() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenThrow(new RuntimeException("Simulated file read error"));
        String result = output.checkResult("write");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
        verify(mockDataReader, times(1)).readLine();
    }

    @Test
    void 받은명령어가_WRITE이고_파일내용이_있으면_ERROR를_반환한다() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_ERROR);
        String result = output.checkResult("write");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
    }

    @Test
    void 받은명령어가_ERASE이고_파일내용이_있으면_ERROR를_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_ERROR);
        String result = output.checkResult("erase");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
    }

    @Test
    void 받은명령어가_Erase이고_파일내용이_비어있으면_DONE을_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_WRITE_PASS);
        String result = output.checkResult("erase");
        assertEquals(RESULT_STRING_WRITE_PASS, result);
    }

    @Test
    void 받은명령어가_flush이고_파일내용이_있으면_ERROR를_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_ERROR);
        String result = output.checkResult("flush");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
    }

    @Test
    void 받은명령어가_flush이고_파일내용이_비어있으면_DONE을_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_WRITE_PASS);
        String result = output.checkResult("flush");
        assertEquals(RESULT_STRING_WRITE_PASS, result);
    }

    @Test
    void 받은명령어가_erase_range이고_파일내용이_있으면_ERROR를_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_ERROR);
        String result = output.checkResult("erase_range");
        assertEquals(RESULT_STRING_WRITE_ERROR, result);
    }

    @Test
    void 받은명령어가_erase_range이고_파일내용이_비어있으면_DONE을_반환() {

        when(mockDataReader.exists()).thenReturn(true);
        when(mockDataReader.readLine()).thenReturn(OUTPUT_TEXT_WRITE_PASS);
        String result = output.checkResult("erase_range");
        assertEquals(RESULT_STRING_WRITE_PASS, result);
    }

}

@Disabled
class ActualTest {
    private Output output;

    @BeforeEach
    void setUp() {
        output = new Output();
    }


    @Test
    void output_파일이_있으면_PASS() throws IOException {

        boolean expected = true;
        boolean act = output.existFileCheck();
        assertEquals(expected, act);

    }

    @Test
    void output_파일이_없으면_FAIL() throws IOException {
        boolean expected = false;
        boolean act = output.existFileCheck();
        assertEquals(expected, act);
    }


    @Test
    void output_파일이_없을때_checkResult_ERROR() throws IOException {

        boolean expected = false;
        String act = output.checkResult("read");
        assertEquals("ERROR", act);

    }


    @Test
    void 받은명령어가_READ이면_OUTPUT파일을_읽는다() throws IOException {

        String expected = "0xFFFFFFF";
        String act = output.checkResult("read");
        assertEquals(expected, act);

    }

    @Test
    void 받은명령어가_write일때_정상동작_확인() throws IOException {

        boolean expected = true;
        String act = output.checkResult("write");
        assertEquals("DONE", act);
    }

    @Test
    void 받은명령어가_write일때_ERROR() throws IOException {
        String expected = "ERROR";
        String act = output.checkResult("write");
        assertEquals(expected, act);
    }

}
