import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SsdTest {
    public static final String DELIMITER = "\t";
    public static final String WRITE_TEST_VALUE = "0xFFFFFFF0";
    public static final int WRITE_TEST_ADDRESS = 88;
    public static final String READ_TEST_VALUE = "0xFFFFFFF0";
    public static final int READ_TEST_ADDRESS = 33;
    public static final String NO_WRITE_VALUE = "0x00000000";
    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final String SSD_NAND_TXT = "ssd_nand.txt";


    @Mock
    Driver fileDriver;

    Ssd ssd;

    @BeforeEach
    void setUp() {
        ssd = new Ssd(fileDriver);

        File file = new File(SSD_OUTPUT_TXT);
        if (file.exists()) file.delete();

        File nand_file = new File(SSD_NAND_TXT);
        if (nand_file.exists()) nand_file.delete();

        doAnswer(invocation -> {
            String path = invocation.getArgument(0);
            byte[] data = invocation.getArgument(1);
            Files.write(Paths.get(path), data);
            return null;
        }).when(fileDriver).write(anyString(), any());

        when(fileDriver.read(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            return new String(Files.readAllBytes(Paths.get(path)));
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
        ssd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);

        //Assert
        verify(fileDriver, times(3)).write(anyString(), any());
        assertTrue(new File(SSD_NAND_TXT).exists(), "파일이 생성되지 않았습니다.");
    }


    @Test
    void LBA_영역_값_쓰기_ssd_nand_txt_존재() throws IOException {
        //Arrange

        //Act
        ssd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);
        ssd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE);

        //Assert
        verify(fileDriver, times(5)).write(anyString(), any());
        assertTrue(new File(SSD_NAND_TXT).exists(), "파일이 생성되지 않았습니다.");
    }


    //Read Test
    @Test
    void 기록한적_있는_LBA영역_읽기() throws IOException {
        //Arrange
        String content = "";

        //Act
        ssd.write(READ_TEST_ADDRESS, READ_TEST_VALUE);
        ssd.read(READ_TEST_ADDRESS);

        //Assert
        content = new String(Files.readAllBytes(Paths.get(SSD_OUTPUT_TXT)));
        verify(fileDriver, times(2)).read(anyString());
        assertThat(content).isEqualTo(READ_TEST_VALUE);
    }

    @Test
        //기록이 한적이 없는 LBA를 읽으면 0x00000000 으로 읽힌다.
    void 기록한적_없는_LBA영역_읽기() throws IOException {
        //Arrange
        String content = "";

        //Act
        ssd.read(READ_TEST_ADDRESS);

        //Assert
        content = new String(Files.readAllBytes(Paths.get(SSD_OUTPUT_TXT)));
        verify(fileDriver, times(1)).read(anyString());
        assertThat(content).isEqualTo(NO_WRITE_VALUE);
    }

//    @Test
//    void 같은_LBA영역_다른_값_쓰고_읽기_여러번() throws IOException {
//
//        //Arrange
//        int retryCnt = 3;
//        doAnswer(invocation -> {
//            String path = invocation.getArgument(0);
//            byte[] data = invocation.getArgument(1);
//            Files.write(Paths.get(SSD_OUTPUT_TXT), data);
//            return null;
//        }).when(fileDriver).write(anyString(), any());
//
//        doAnswer(invocation -> {
//            byte[] data = Files.readAllBytes(Paths.get(SSD_OUTPUT_TXT));
//            try (FileOutputStream fos = new FileOutputStream(SSD_OUTPUT_TXT)) {
//                fos.write(data);
//                return null;
//            }
//        }).when(fileDriver).read(anyString());
//
//        //Act
//        List<String> readStringList = new ArrayList<>();
//        for (int i = 0; i < retryCnt; i++) {
//            ssd.write(WRITE_TEST_ADDRESS, WRITE_TEST_VALUE + i);
//            ssd.read(WRITE_TEST_ADDRESS);
//            readStringList.add(Files.readString(Paths.get(SSD_OUTPUT_TXT)));
//        }
//
//        //Assert
//        for (int i = 0; i < readStringList.size(); i++) {
//            assertThat(readStringList.get(i)).isEqualTo(String.valueOf(//                fileOutputStream.write((WRITE_TEST_ADDRESS + DELIMITER + (WRITE_TEST_VALUE+i)).getBytes());));
//        }
//        verify(fileDriver, times(retryCnt)).write(anyString(), any());
//        verify(fileDriver, times(retryCnt)).read(anyString());
//    }
}
