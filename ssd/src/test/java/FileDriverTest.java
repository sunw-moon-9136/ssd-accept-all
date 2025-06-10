import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDriverTest {

    @Test
    void 파일명을_null으로_파일_read를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.read(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 파일명을_empty_string_으로_파일_read를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.read(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 파일명을_null으로_파일_write를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.write(null, new byte[]{1, 2, 3, 4}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 파일명을_empty_string_으로_파일_write를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.write("", new byte[]{1, 2, 3, 4}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_종류의_파일_read를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.read("no_file.txt"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_종류의_파일_write를_수행한_경우() {
        FileDriver fileDriver = new FileDriver();

        assertThatThrownBy(() -> fileDriver.write("no_file.txt", new byte[]{1, 2, 3, 4}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}