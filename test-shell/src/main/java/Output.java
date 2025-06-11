import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Output {

    final static String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";

    public boolean existFileCheck() {

        try {
            Path path = Paths.get(OUTPUT_FILE_PATH);
            return (Files.exists(path));
        } catch (Exception e) {
            System.out.println("OUTPUT FILE ERROR");
            return false;
        }


    }

    public String readLine() {
        String result;

        return "T";

    }
}
