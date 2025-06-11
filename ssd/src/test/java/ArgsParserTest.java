import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArgsParserTest {
    // TODO RuntimeException -> output.txt.에 Error를 저장하는 것으로 변경

    ArgsParser parser;

    @Mock
    Driver mockDriver;

    @BeforeEach
    void setUp() {
        parser = new ArgsParser();
        parser.setDriver(mockDriver);
    }

    private static final String INVALID_FIRST_ARG[] = {"Q", "12"};
    private static final String VALID_READ_ARGS[] = {"R", "56"};
    private static final String INVALID_READ_ARGS_CNT[] = {"R", "12", "77", "(!"};

    @Test
    void ArgsParser에서_에러가나면_Driver_Write를_호출() {
        doNothing().when(mockDriver).write(anyString(), any());

        parser.error();

        verify(mockDriver, times(1)).write(anyString(), any());
    }

    @Test
    void 인자의_맨앞이_R_혹은_W가_아니면_에러() {
        doNothing().when(mockDriver).write(anyString(), any());

        parser.run(INVALID_FIRST_ARG);

        verify(mockDriver, times(1)).write(anyString(), any());
    }

    @Test
    void 읽기요청의_인자는2개_입력값2개일때() {
        parser.run(VALID_READ_ARGS);

        verify(mockDriver, never()).write(anyString(), any());
    }

    @Test
    void 읽기요청의_인자는2개_입력값2개가아닐때_에러() {
        doNothing().when(mockDriver).write(anyString(), any());

        parser.run(INVALID_READ_ARGS_CNT);

        verify(mockDriver, times(1)).write(anyString(), any());
    }

    @Test
    @Disabled
    void 쓰기요청의_인자는3개_입력값3개일때() {

    }

    @Test
    @Disabled
    void 쓰기요청의_인자는3개_입력값1개일때_에러() {

    }

    @Test
    @Disabled
    void LBA의_범위는_0_100사이_입력값이_범위안일때() {

    }

    @Test
    @Disabled
    void LBA의_범위는_0_100사이_입력값이_범위밖일때_에러() {

    }

    @Test
    @Disabled
    void 값의자리수는10_입력값_자리수가_10이아닐때_에러() {

    }

    @Test
    @Disabled
    void 값의시작부분이_0x일때() {

    }

    @Test
    @Disabled
    void 값의시작부분이_0x가아니면_에러() {

    }
}