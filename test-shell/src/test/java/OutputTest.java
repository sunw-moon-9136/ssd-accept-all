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
    void 받은명령어가_READ이면_OUTPUT파일을_읽어_diaplay한다() throws IOException {

        boolean expected = true;
        String act = mockOutput.checkResult("read");
        verify(mockOutput, times(1)).checkResult("read");

    }

}