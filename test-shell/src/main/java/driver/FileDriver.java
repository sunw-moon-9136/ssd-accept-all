package driver;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FileDriver implements Driver {

    public static final String LOG_EXTENSION_POSTFIX = ".log";

    @Override
    public void createDirectoryIfAbsent(String directoryName) {
        Path dirPath = Path.of(directoryName);
        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) return;

        try {
            Files.createDirectory(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    @Override
    public void append(String file, byte[] bytes) {
        requireValidFileName(file);

        try {
            Files.write(Paths.get(file), bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    @Override
    public void changeOldLogFileName(String latestLogFileName, String directoryName) {
        List<String> logFileNames = getLogFileNames(directoryName).stream()
                .filter(name -> !name.endsWith(".zip"))
                .filter(name -> !name.equals(latestLogFileName))
                .sorted()
                .collect(Collectors.toList());

        int size = logFileNames.size();
        if (size <= 1) return;

        logFileNames.remove(size - 1);
        for (String oldLogFileName : logFileNames) {
            convertToZipFile(oldLogFileName, directoryName);
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
            if (fileSize > maxSize)
                Files.move(filePath, getTargetPath.get(), StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new RuntimeException("File Not Found: " + source);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    //    @VisibleForTesting
    void convertToZipFile(String oldLogFileName, String directoryName) {
        try {
            String zipFileName = oldLogFileName.replace(".log", ".zip");
            Files.move(Path.of(directoryName + "/" + oldLogFileName), Path.of(directoryName + "/" + zipFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new RuntimeException("File Not Found: " + oldLogFileName);
        } catch (IOException e) {
            throw new RuntimeException("Not Expected Error");
        }
    }

    // @VisibleForTesting
    List<String> getLogFileNames(String directoryName) {
        // 참조 : 협업을 위해 JAVA NIO + Stream API 사용을 피해 JAVA IO를 사용함
        File[] files = new File(directoryName).listFiles();

        if (files == null) {
            return List.of();
        }

        List<String> fileNames = new ArrayList<>();
        for (File file : files)
            if (file.isFile() && file.getName().endsWith(LOG_EXTENSION_POSTFIX))
                fileNames.add(file.getName());
        return fileNames;
    }

    private void requireValidFileName(String file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Invalid Argument");
    }
}
