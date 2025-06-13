package utils;

import logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class LogFileDeleter {
    public static void deleteRecursivelyLogDirectory() throws IOException {
        Path directoryToDelete = Path.of(Logger.LOG_DIRECTORY_NAME);

        if (Files.exists(directoryToDelete)) {
            Files.walk(directoryToDelete)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }
}
