import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


}