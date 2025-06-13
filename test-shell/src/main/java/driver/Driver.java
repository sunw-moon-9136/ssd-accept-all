package driver;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface Driver {

    void append(String file, byte[] bytes);

    void changeOldLogFileName(String latestLogFileName);

    void changeNameIfBiggerThan(long maxSize, String source, Supplier<Path> getTargetPath);
}
