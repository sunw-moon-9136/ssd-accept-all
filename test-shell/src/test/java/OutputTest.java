import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputTest {

    @Test
    void output_파일이_있으면_PASS() {
        Output o = new Output();
        boolean expected = true;
        boolean act = o.existFileCheck();
        assertEquals(expected, act);

    }


}