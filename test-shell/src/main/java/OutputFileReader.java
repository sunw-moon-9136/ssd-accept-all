import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputFileReader implements DataReader {

    private final Path path;

    public OutputFileReader(String filePath) {
        this.path = Paths.get(filePath);
    }

    @Override
    public boolean exists() {
        try {
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String readLine() {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.readLine();
        } catch (IOException e) {
            //  System.out.println("파일 읽기 오류");
            return "ERROR";
        }
    }
}