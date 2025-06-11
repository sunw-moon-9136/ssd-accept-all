import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SsdControllerTest {
    // TODO RuntimeException -> output.txt.에 Error를 저장하는 것으로 변경
    private static final String[] INVALID_FIRST_ARG = {"Q", "12"};
    private static final String[] VALID_READ_ARGS = {"R", "56"};
    private static final String[] INVALID_READ_ARGS_CNT = {"R", "12", "77", "(!"};
    private static final String[] VALID_WRITE_ARGS = {"W", "56", "0x12345678"};
    private static final String[] INVALID_WRITE_ARGS_CNT = {"W", "15"};
    private static final String[] INVALID_READ_LBA = {"R", "100"};
    private static final String[] INVALID_WRITE_LBA = {"W", "-1", "0x12345678"};
    private static final String[] INVALID_LBA_CHARACTER = {"W", "qvione", "0x12345678"};
    private static final String[] INVALID_VALUE_LENGTH = {"W", "12", "0x1234567194538"};
    private static final String[] INVALID_VALUE_CHARACTER = {"W", "12", "0(!*@&$(@*#"};

    SsdController controller;

    @Mock
    Driver mockDriver;

    @Mock
    ReadWritable mockDisk;

    @Nested
    @DisplayName("인자 값 유효성 검사 테스트")
    class ArgumentValidation {
        @BeforeEach
        void setUp() {
            controller = new SsdController();
            controller.setDriver(mockDriver);
        }

        @Test
        void SsdController에서_에러가나면_Driver_Write를_호출() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.error();

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void 인자의_맨앞이_R_혹은_W가_아니면_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_FIRST_ARG);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void 읽기요청의_인자는2개_입력값2개일때() {
            controller.run(VALID_READ_ARGS);

            verify(mockDriver, never()).write(anyString(), any());
        }

        @Test
        void 읽기요청의_인자는2개_입력값2개가아닐때_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_READ_ARGS_CNT);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void 쓰기요청의_인자는3개_입력값3개일때() {
            controller.run(VALID_WRITE_ARGS);

            verify(mockDriver, never()).write(anyString(), any());
        }

        @Test
        void 쓰기요청의_인자는3개_입력값2개일때_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_WRITE_ARGS_CNT);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void LBA의_범위는_0_99사이_입력값이_범위안일때() {
            controller.run(VALID_READ_ARGS);

            verify(mockDriver, never()).write(anyString(), any());
        }

        @Test
        void LBA의_범위는_0_99사이_읽기_입력값이_범위밖일때_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_READ_LBA);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void LBA의_범위는_0_99사이_쓰기_입력값이_범위밖일때_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_WRITE_LBA);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void LBA에_문자가_들어오면_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_LBA_CHARACTER);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void 값의자리수는10_입력값_자리수가_10이아닐때_에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_VALUE_LENGTH);

            verify(mockDriver, times(1)).write(anyString(), any());
        }

        @Test
        void 값의시작부분이_0x일때() {
            controller.run(VALID_WRITE_ARGS);

            verify(mockDriver, never()).write(anyString(), any());
        }

        @Test
        void 값의시작부분이_0x가아니거나_이상한문자가오면에러() {
            doNothing().when(mockDriver).write(anyString(), any());

            controller.run(INVALID_VALUE_CHARACTER);

            verify(mockDriver, times(1)).write(anyString(), any());
        }
    }

    @Nested
    @DisplayName("읽기 테스트")
    class ReadTest {
        @BeforeEach
        void setUp() {
            controller = new SsdController(mockDriver, mockDisk);
        }

        @Test
        void 읽기_호출_시_ssd_read를_호출한다() {
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(mockDisk).read(anyInt()); // equals(anyString()) 제거
        }

        @Test
        void 읽기_호출_후_Driver를_호출하여_output에_기록() {
            doNothing().when(mockDriver).write(anyString(), any());
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(mockDisk).read(anyInt());
            verify(mockDriver).write(anyString(), any());
        }

        @Test
        void 쓰기_호출_시_ssd_write를_호출한다() {
            doNothing().when(mockDisk).write(anyInt(), anyString());

            controller.run(VALID_WRITE_ARGS);

            verify(mockDisk).write(anyInt(),anyString()); // equals(anyString()) 제거
        }
    }
}
