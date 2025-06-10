import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OutputTest {

    @Mock
    Output f;

    @Test
    void output_파일이_있으면_PASS() throws IOException {
        Output o = new Output();
        boolean expected = true;
        boolean act = o.existFileCheck();
        assertEquals(expected, act);
    }

    @Test
    void output_파일이_없으면_FAIL() throws IOException {
        when(f.existFileCheck()).thenReturn(false);
        boolean expected = false;
        boolean act = f.existFileCheck();
        assertEquals(expected, act);
    }


}