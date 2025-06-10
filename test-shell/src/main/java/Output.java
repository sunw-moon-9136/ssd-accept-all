import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Output {

    final static String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";

    public boolean existFileCheck() throws IOException {

        Path path = Paths.get(OUTPUT_FILE_PATH);

        if (Files.exists(path)) {
            return true;
        } else {
            return false;
        }
    }

}
