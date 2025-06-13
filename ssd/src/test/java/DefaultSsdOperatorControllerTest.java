import NAND.ReadWritable;
import SSD.OutputHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
class DefaultSsdOperatorControllerTest {
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

    SsdManager controller;

    @Mock
    OutputHandler outputHandler;

    @Mock
    ReadWritable mockDisk;

    @BeforeEach
    void setUp() {
//        controller = new SsdManager(mockDriver, mockDisk);
    }


    @Nested
    @DisplayName("인자 값 유효성 검사 테스트")
    class ArgumentValidation {
        @Test
        void SsdController에서_에러가나면_Driver_Write를_호출() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.error();

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 인자의_맨앞이_R_혹은_W가_아니면_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_FIRST_ARG);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 읽기요청의_인자는2개_입력값2개일때() {
            doNothing().when(outputHandler).write(anyString(), any());
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(outputHandler, times(1)).write(anyString(), any());
            verify(mockDisk, times(1)).read(anyInt());
        }

        @Test
        void 읽기요청의_인자는2개_입력값2개가아닐때_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_READ_ARGS_CNT);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 쓰기요청의_인자는3개_입력값3개일때() {
            doNothing().when(outputHandler).write(anyString(), any());
            doNothing().when(mockDisk).write(anyInt(), anyString());

            controller.run(VALID_WRITE_ARGS);

            verify(outputHandler, times(1)).write(anyString(), any());
            verify(mockDisk, times(1)).write(anyInt(), anyString());
        }

        @Test
        void 쓰기요청의_인자는3개_입력값2개일때_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_WRITE_ARGS_CNT);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void LBA의_범위는_0_99사이_입력값이_범위안일때() {
            doNothing().when(outputHandler).write(anyString(), any());
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(outputHandler, times(1)).write(anyString(), any());
            verify(mockDisk, times(1)).read(anyInt());
        }

        @Test
        void LBA의_범위는_0_99사이_읽기_입력값이_범위밖일때_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_READ_LBA);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void LBA의_범위는_0_99사이_쓰기_입력값이_범위밖일때_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_WRITE_LBA);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void LBA에_문자가_들어오면_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_LBA_CHARACTER);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 값의자리수는10_입력값_자리수가_10이아닐때_에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_VALUE_LENGTH);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 값의시작부분이_0x일때() {
            doNothing().when(outputHandler).write(anyString(), any());
            doNothing().when(mockDisk).write(anyInt(), anyString());

            controller.run(VALID_WRITE_ARGS);

            verify(outputHandler, times(1)).write(anyString(), any());
            verify(mockDisk, times(1)).write(anyInt(), anyString());
        }

        @Test
        void 값의시작부분이_0x가아니거나_이상한문자가오면에러() {
            doNothing().when(outputHandler).write(anyString(), any());

            controller.run(INVALID_VALUE_CHARACTER);

            verify(outputHandler, times(1)).write(anyString(), any());
        }
    }

    @Nested
    @DisplayName("읽기 테스트")
    class ReadTest {
        @Test
        void 읽기_호출_시_ssd_read를_호출한다() {
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(mockDisk, times(1)).read(anyInt()); // equals(anyString()) 제거
        }

        @Test
        void 읽기_호출_후_Driver를_호출하여_output에_기록() {
            doNothing().when(outputHandler).write(anyString(), any());
            when(mockDisk.read(anyInt())).thenReturn(anyString());

            controller.run(VALID_READ_ARGS);

            verify(mockDisk, times(1)).read(anyInt());
            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 쓰기_호출_시_ssd_write를_호출한다() {
            doNothing().when(mockDisk).write(anyInt(), anyString());

            controller.run(VALID_WRITE_ARGS);

            verify(mockDisk, times(1)).write(anyInt(), anyString()); // equals(anyString()) 제거
        }

        @Test
        void 쓰기_호출_시_ssd_nand를_flush해야한다() {
            doNothing().when(mockDisk).write(anyInt(), anyString());

            controller.run(VALID_WRITE_ARGS);

            verify(mockDisk, times(1)).write(anyInt(), anyString());
            verify(outputHandler, times(1)).write(anyString(), any());
        }
    }

    @Nested
    @DisplayName("지우기 테스트")
    class EraseTest {
        @Test
        void 지우기_명령이_제대로_들어왔는가() {
            doNothing().when(mockDisk).erase(anyInt(), anyInt());
            String[] args = {"E", "12", "3"};

            controller.run(args);

            verify(mockDisk, times(1)).erase(anyInt(), anyInt());
        }

        @Test
        void 지우기_명령이_이상하게_뜰어오면() {
            doNothing().when(outputHandler).write(anyString(), any());
            String[] args = {"E", "96", "5"};

            controller.run(args);

            verify(outputHandler, times(1)).write(anyString(), any());
        }

        @Test
        void 지우기_명령이_이상하게_뜰어오면_size가_10이상() {
            doNothing().when(outputHandler).write(anyString(), any());
            String[] args = {"E", "55", "14"};

            controller.run(args);

            verify(outputHandler, times(1)).write(anyString(), any());
        }
    }
}
