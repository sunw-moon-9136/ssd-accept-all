import java.util.Objects;

public class ArgsParser {
    public static void main(String[] args) {
        if (args[0].equals("R") || args[0].equals("W")) {
;
        } else {
            throw new RuntimeException();
        }
    }
}
