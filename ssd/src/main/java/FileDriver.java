import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileDriver implements Driver {

    public static final String NAND_FILE_NAME = "ssd_nand.txt";
    public static final String OUTPUT_FILE_NAME = "ssd_output.txt";

    @Override
    public String read(String file) {
        requireValidFileName(file);

        return "";
    }

    @Override
    public void write(String file, byte[] bytes) {
        requireValidFileName(file);

        try {
            Path path = Paths.get(file);
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requireValidFileName(String file) {
        if (isNullOrEmpty(file) || !(file.equals(OUTPUT_FILE_NAME) || file.equals(NAND_FILE_NAME)))
            throw new IllegalArgumentException();
    }

    private boolean isNullOrEmpty(String file) {
        return file == null || file.isEmpty();
    }
}
