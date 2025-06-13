package SSD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class OutputFileHandler implements OutputHandler {
    public static final String OUTPUT_FILE_NAME = "ssd_output.txt";

    @Override
    public String read(String file) {
        if (!isValidFileName(file)) {
            throw new IllegalArgumentException("Invalid File Name");
        }

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
        if (!isValidFileName(file)) {
            throw new IllegalArgumentException("Invalid File Name");
        }

        try {
            Files.write(Paths.get(file), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidFileName(String file) {
        return !isNullOrEmpty(file) && (file.equals(OUTPUT_FILE_NAME));
    }

    private boolean isNullOrEmpty(String file) {
        return file == null || file.isEmpty();
    }
}
