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
        if (!isValidFileName(file))
            throw new IllegalArgumentException();

        return "";
    }

    @Override
    public void write(String file, byte[] bytes) {
        if (!isValidFileName(file))
            throw new IllegalArgumentException();

        Path path = Paths.get(file);
        try {
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidFileName(String file) {
        return !isNullOrEmpty(file) &&
                (file.equals(OUTPUT_FILE_NAME) || file.equals(NAND_FILE_NAME));
    }

    private boolean isNullOrEmpty(String file) {
        return file == null || file.isEmpty();
    }
}
