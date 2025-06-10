import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest {
    // TODO RuntimeException -> output.txt.에 Error를 저장하는 것으로 변경

    @Test
    void 인자의_맨앞이_R_혹은_W가_아니면_에러() {
        String args[] = {"Q", "12"};

        assertThrows(RuntimeException.class, () -> ArgsParser.main(args));
    }

    @Test
    void 읽기요청의_인자는2개_입력값2개일때() {

    }

    @Test
    void 읽기요청의_인자는2개_입력값2개가아닐때_에러() {

    }

    @Test
    void 쓰기요청의_인자는3개_입력값3개일때() {

    }

    @Test
    void 쓰기요청의_인자는3개_입력값1개일때_에러() {

    }

    @Test
    void LBA의_범위는_0_100사이_입력값이_범위안일때() {

    }

    @Test
    void LBA의_범위는_0_100사이_입력값이_범위밖일때_에러() {

    }

    @Test
    void 값의자리수는10_입력값_자리수가_10이아닐때_에러() {

    }

    @Test
    void 값의시작부분이_0x일때() {

    }

    @Test
    void 값의시작부분이_0x가아니면_에러() {

    }
}