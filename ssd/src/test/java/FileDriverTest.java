import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDriverTest {

    public static final byte[] TEST_BYTES = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private FileDriver fileDriver;

    @BeforeEach
    void setUp() {
        fileDriver = new FileDriver();
    }

    @Nested
    class FileNameExceptionTest {

        public static final String INVALID_FILE_NAME = "no_file.txt";

        @Test
        void 파일명을_null으로_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_empty_string_으로_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_null으로_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write(null, TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 파일명을_empty_string_으로_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write("", TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 잘못된_종류의_파일_read를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.read(INVALID_FILE_NAME))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 잘못된_종류의_파일_write를_수행한_경우() {
            assertThatThrownBy(() -> fileDriver.write(INVALID_FILE_NAME, TEST_BYTES))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class WriteTest {

        public static final String NOT_IMPORTANT_ALREADY_EXIST_OUTPUT_TEXT = "Bye Bye, World!";

        @Test
        void 대상_파일이_존재하지_않는_경우_파일_새로_생성하여_write() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.deleteIfExists(path);

            fileDriver.write(FileDriver.NAND_FILE_NAME, TEST_BYTES);

            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.readString(path)).isEqualTo(new String(TEST_BYTES, StandardCharsets.UTF_8));
        }

        @Test
        void 대상_파일이_존재하는_경우_파일_덮어쓰기하여_write() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.writeString(path, NOT_IMPORTANT_ALREADY_EXIST_OUTPUT_TEXT,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            fileDriver.write(FileDriver.NAND_FILE_NAME, TEST_BYTES);

            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.readString(path)).isEqualTo(new String(TEST_BYTES, StandardCharsets.UTF_8));
        }
    }

    @Nested
    class ReadTest {
        
        public static final String NAND_INIT_TEXT = "0 0x00000000\n1 0x00000000\n2 0x00000000\n3 0x00000000\n4 0x00000000\n5 0x00000000\n6 0x00000000\n7 0x00000000\n8 0x00000000\n9 0x00000000\n10 0x00000000\n11 0x00000000\n12 0x00000000\n13 0x00000000\n14 0x00000000\n15 0x00000000\n16 0x00000000\n17 0x00000000\n18 0x00000000\n19 0x00000000\n20 0x00000000\n21 0x00000000\n22 0x00000000\n23 0x00000000\n24 0x00000000\n25 0x00000000\n26 0x00000000\n27 0x00000000\n28 0x00000000\n29 0x00000000\n30 0x00000000\n31 0x00000000\n32 0x00000000\n33 0x00000000\n34 0x00000000\n35 0x00000000\n36 0x00000000\n37 0x00000000\n38 0x00000000\n39 0x00000000\n40 0x00000000\n41 0x00000000\n42 0x00000000\n43 0x00000000\n44 0x00000000\n45 0x00000000\n46 0x00000000\n47 0x00000000\n48 0x00000000\n49 0x00000000\n50 0x00000000\n51 0x00000000\n52 0x00000000\n53 0x00000000\n54 0x00000000\n55 0x00000000\n56 0x00000000\n57 0x00000000\n58 0x00000000\n59 0x00000000\n60 0x00000000\n61 0x00000000\n62 0x00000000\n63 0x00000000\n64 0x00000000\n65 0x00000000\n66 0x00000000\n67 0x00000000\n68 0x00000000\n69 0x00000000\n70 0x00000000\n71 0x00000000\n72 0x00000000\n73 0x00000000\n74 0x00000000\n75 0x00000000\n76 0x00000000\n77 0x00000000\n78 0x00000000\n79 0x00000000\n80 0x00000000\n81 0x00000000\n82 0x00000000\n83 0x00000000\n84 0x00000000\n85 0x00000000\n86 0x00000000\n87 0x00000000\n88 0x00000000\n89 0x00000000\n90 0x00000000\n91 0x00000000\n92 0x00000000\n93 0x00000000\n94 0x00000000\n95 0x00000000\n96 0x00000000\n97 0x00000000\n98 0x00000000\n99 0x00000000";
        public static final String TEST_TEXT = "TEST-TEXT";

        @Test
        void 대상_파일이_존재하는_경우_파일_전체_read() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.writeString(path, TEST_TEXT, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String actual = fileDriver.read(FileDriver.NAND_FILE_NAME);

            assertThat(actual).isEqualTo(TEST_TEXT);
        }

        @Test
        void 대상_파일이_존재하지_않는_경우_NAND_FILE_INIT하여_신규로_생성_후_return() throws IOException {
            Path path = Paths.get(FileDriver.NAND_FILE_NAME);
            Files.deleteIfExists(path);

            String actual = fileDriver.read(FileDriver.NAND_FILE_NAME);

            assertThat(actual).isEqualTo(NAND_INIT_TEXT);
            assertThat(Files.exists(path)).isTrue();
            assertThat(Files.readString(path)).isEqualTo(NAND_INIT_TEXT);
        }
    }
}