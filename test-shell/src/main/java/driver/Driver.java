package driver;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface Driver {

    void createDirectoryIfAbsent(String directoryName);

    void append(String file, byte[] bytes);

    void changeOldLogFileName(String latestLogFileName, String directoryName);

    void changeNameIfBiggerThan(long maxSize, String source, Supplier<Path> getTargetPath);
}
