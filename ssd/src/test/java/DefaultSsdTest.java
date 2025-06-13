import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultSsdTest {
    public static final String DELIMITER = "\t";
    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final String SSD_NAND_TXT = "ssd_nand.txt";

    public static final int WRITE_TEST_ADDRESS = 88;
    public static final int READ_TEST_ADDRESS = 33;
    public static final String WRITE_TEST_VALUE = "0xFFFFFFF0";
    public static final String READ_TEST_VALUE = "0xFFFFFFF0";
    public static final String NO_WRITE_VALUE = "0x00000000";
    public static final String INIT_VALUE = "0x00000000";


    @Mock
    Driver fileDriver;

    DefaultSsd defaultSsd;

    @BeforeEach
    void setUp() throws IOException {
        defaultSsd = new DefaultSsd(fileDriver);

        Files.deleteIfExists(Paths.get(SSD_OUTPUT_TXT));
        Files.deleteIfExists(Paths.get(SSD_NAND_TXT));

        doAnswer(invocation -> {
            String path = invocation.getArgument(0);
            byte[] data = invocation.getArgument(1);
            Files.write(Paths.get(path), data);
            return null;
        }).when(fileDriver).write(anyString(), any());

        when(fileDriver.read(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            try {
                return new String(Files.readAllBytes(Paths.get(path)));
            } catch (NoSuchFileException e) {
                throw new RuntimeException("no file");
            }
        });
    }

    @AfterEach
    void cleanup() {
        File output_file = new File(SSD_OUTPUT_TXT);
        if (output_file.exists()) output_file.delete();

        File nand_file = new File(SSD_NAND_TXT);
        if (nand_file.exists()) nand_file.delete();
    }

    //Write Test
    @Test
    void LBA_영역_값_쓰기_ssd_nand_txt_미존재() throws IOException {
        //Arrange
        //Act
        defaultSsd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);

        //Assert
        assertTrue(new File(SSD_NAND_TXT).exists(), "파일이 생성되지 않았습니다.");
    }


    @Test
    void LBA_영역_값_쓰기_ssd_nand_txt_존재() throws IOException {
        //Arrange

        //Act
        defaultSsd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);
        defaultSsd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);

        //Assert
        assertTrue(new File(SSD_NAND_TXT).exists(), "파일이 생성되지 않았습니다.");
    }


    //Read Test
    @Test
    void 기록한적_있는_LBA영역_읽기() throws IOException {
        //Arrange
        String content = "";

        //Act
        defaultSsd.write(READ_TEST_ADDRESS, READ_TEST_VALUE);
        content = defaultSsd.read(READ_TEST_ADDRESS);

        //Assert
        assertThat(content).isEqualTo(READ_TEST_VALUE);
    }

    @Test
        //기록이 한적이 없는 LBA를 읽으면 0x00000000 으로 읽힌다.
    void 기록한적_없는_LBA영역_읽기() throws IOException {
        //Arrange
        String content = "";

        //Act
        content = defaultSsd.read(READ_TEST_ADDRESS);

        //Assert
        assertThat(content).isEqualTo(NO_WRITE_VALUE);
    }

    @Test
    void 같은_LBA영역_다른_값_쓰고_읽기_여러번() throws IOException {
        //Arrange
        int retryCnt = 3;

        //Act
        List<String> readStringList = new ArrayList<>();
        for (int i = 0; i < retryCnt; i++) {
            defaultSsd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE + i);
            String content = defaultSsd.read(WRITE_TEST_ADDRESS);
            readStringList.add(content);
        }

        //Assert
        for (int i = 0; i < readStringList.size(); i++) {
            assertThat(readStringList.get(i)).isEqualTo(String.valueOf(WRITE_TEST_VALUE + i));
        }
    }

    @Test
    void 지우는_크기_0일때_아무변화없음() {
        //Arrange
        int writeSize = 5;
        int eraseSize = 0;
        for (int i = 0; i < writeSize; i++) {
            defaultSsd.write(WRITE_TEST_ADDRESS + i, WRITE_TEST_VALUE);
        }

        //Act
        defaultSsd.erase(WRITE_TEST_ADDRESS, eraseSize);

        //Assert
        assertThat(defaultSsd.read(WRITE_TEST_ADDRESS)).isEqualTo(WRITE_TEST_VALUE);
    }

    @Test
    void 지우는_크기_3일때_해당범위_초기화() {
        //Arrange
        int writeSize = 5;
        int eraseSize = 3;
        for (int i = 0; i < writeSize; i++) {
            defaultSsd.write(WRITE_TEST_ADDRESS + i, WRITE_TEST_VALUE);
        }

        //Act
        defaultSsd.erase(WRITE_TEST_ADDRESS, eraseSize);

        //Assert
        for (int i = 0; i < writeSize; i++) {
            if (i < eraseSize) assertThat(defaultSsd.read(WRITE_TEST_ADDRESS + i)).isEqualTo(NO_WRITE_VALUE);
            else assertThat(defaultSsd.read(WRITE_TEST_ADDRESS + i)).isEqualTo(WRITE_TEST_VALUE);
        }
    }

}
