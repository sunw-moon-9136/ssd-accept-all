import java.io.InputStream;
import java.util.Scanner;

public class TestShell {
    public String runTestShell(InputStream input) {
        System.out.println("=== SSD Shell ===");

        Scanner scanner = new Scanner(input);
        while (true) {
            System.out.print(">> ");
            String line = scanner.nextLine().trim();

            switch (line) {
                case "read" -> {
                    return "read";
                }
                case "write" -> {
                    return "write";
                }
                case "fullread" -> {
                    return "fullread";
                }
                case "fullwrite" -> {
                    return "fullwrite";
                }
                case "help" -> {
                    return "help";
                }
                case "exit" -> {
                    return "exit";
                }
                default -> {
                    return "notCommand";
                }
            }
        }
    }
}
