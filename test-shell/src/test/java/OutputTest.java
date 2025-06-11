import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OutputTest {

    @Mock
    Output mockOutput;


    @Test
    void output_파일이_있으면_PASS() throws IOException {
        Output output = new Output();
        boolean expected = true;
        boolean act = output.existFileCheck();
        assertEquals(expected, act);
    }

    @Test
    void output_파일이_없으면_FAIL() throws IOException {

        when(mockOutput.existFileCheck()).thenReturn(false);
        boolean expected = false;
        boolean act = mockOutput.existFileCheck();
        assertEquals(expected, act);
    }

    @Test
    void output_파일이_있으면_마지막줄을_읽어서준다() throws IOException {

        Output output = new Output();
        boolean expected = true;
        String act = output.readLine();
        assertNotNull(act);
    }

    @Test
    void 수행한_명령어를_받고_display한다() throws IOException {

        boolean expected = true;
        String act = mockOutput.checkResult("read");
        verify(mockOutput, times(1)).checkResult("read");

    }

    @Test
    void 받은명령어가_READ이면_OUTPUT파일을_check한다() throws IOException {

        boolean expected = true;
        String act = mockOutput.checkResult("read");
        verify(mockOutput, times(1)).checkResult("read");

    }

    @Test
    void 받은명령어가_READ이면_OUTPUT파일을_읽는다() throws IOException {

        Output output = new Output();
        boolean expected = true;
        String act = output.checkResult("read");
        System.out.println(act);
        assertNotNull(act);

    }

    @Test
    void 받은명령어가_READ이면_읽은_OUTPUT파일을_display() throws IOException {

        Output output = new Output();
        boolean expected = true;
        String act = output.checkResult("read");

        assertNotNull(act);
    }

    //To-Do
    @Test
    void READ명령을_3번받으면_3번_출력() throws IOException {


        mockOutput.checkResult("read");
        mockOutput.checkResult("read");
        mockOutput.checkResult("read");
        verify(mockOutput, times(3)).checkResult("read");

    }


    @Test
    void 받은명렁어가_Write이면_OUTPUT파일을_check한다() throws IOException {

        boolean expected = true;
        String act = mockOutput.checkResult("write");
        verify(mockOutput, times(1)).checkResult("write");

    }

    @Test
    void 받은명령어가_write이면_OUTPUT파일을_읽는다() throws IOException {

        Output output = new Output();
        boolean expected = true;
        String act = output.checkResult("write");
        System.out.println(act);
        assertNotNull(act);
    }


    @Test
    void 받은명령어가_write일때_정상동작_확인() throws IOException {

        Output output = new Output();
        boolean expected = true;
        String act = output.checkResult("write");
        System.out.println(act);
        assertEquals(null, act);
    }

}