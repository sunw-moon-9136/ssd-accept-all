package driver;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface Driver {

    String read(String file);

    void write(String file, byte[] bytes);

    void changeNameIfBiggerThan(long maxSize, String source, Supplier<Path> getTargetPath);
}
