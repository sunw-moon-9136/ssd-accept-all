import java.util.Scanner;

public class TestShell {
    public String runTestShell(Scanner input) {
        System.out.print(">> ");
        String command = input.nextLine().trim();

        switch (command) {
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

