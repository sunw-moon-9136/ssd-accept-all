import java.io.IOException;
import java.nio.file.*;

public class FileDriver implements Driver {

    public static final String NAND_FILE_NAME = "ssd_nand.txt";
    public static final String OUTPUT_FILE_NAME = "ssd_output.txt";

    @Override
    public String read(String file) {
        requireValidFileName(file);

         try {
            return Files.readString(Paths.get(file));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("File Not Found: " + file);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    @Override
    public void write(String file, byte[] bytes) {
        requireValidFileName(file);

        try {
            Files.write(Paths.get(file), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requireValidFileName(String file) {
        if (isNullOrEmpty(file) || !(file.equals(OUTPUT_FILE_NAME) || file.equals(NAND_FILE_NAME)))
            throw new IllegalArgumentException("Invalid Argument");
    }

    private boolean isNullOrEmpty(String file) {
        return file == null || file.isEmpty();
    }
}
