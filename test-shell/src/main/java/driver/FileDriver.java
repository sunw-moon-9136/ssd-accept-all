package driver;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Supplier;

public class FileDriver implements Driver {

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

    @Override
    public void changeNameIfBiggerThan(long maxSize, String source, Supplier<Path> getTargetPath) {
        requireValidFileName(source);
        if (maxSize <= 0L) throw new RuntimeException("MaxFileSize is Invalid: " + maxSize);
        if (getTargetPath == null) throw new RuntimeException("GetTargetPath Supplier is Null");

        try {
            Path filePath = Path.of(source);
            long fileSize = Files.size(filePath);
            if (fileSize > maxSize) Files.move(filePath, getTargetPath.get(), StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new RuntimeException("File Not Found: " + source);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    private void requireValidFileName(String file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Invalid Argument");
    }
}
