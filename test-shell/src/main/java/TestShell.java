import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class TestShell {
    public static final String INVALID_COMMAND = "INVALID COMMAND";

    public String runTestShell(Scanner input) {
        System.out.print(">> ");
        String command = input.nextLine().trim();

        if (!validCommand(command)) return INVALID_COMMAND;
        return command;
    }

    private boolean validCommand(String command) {
        if (command == null || command.isEmpty()) return false;

        String[] parts = command.split("\\s+");
        if (!checkRegisteredCommand(parts[0])) return false;
        if (!parts[0].matches("[a-z]+")) return false;

        if (Arrays.asList("fullread", "help", "exit").contains(parts[0]) && parts.length != 1) return false;
        if (Objects.equals(parts[0], "read") && (!isValidRead(parts))) return false;
        if (Objects.equals(parts[0], "fullwrite") && (!isValidFullwrite(parts))) return false;
        if (Objects.equals(parts[0], "write") && (!isValidWrite(parts))) return false;

        return true;
    }

    private boolean checkRegisteredCommand(String command) {
        String[] candidate = {"read", "write", "fullread", "fullwrite", "help", "exit"};
        return Arrays.asList(candidate).contains(command);
    }

    private boolean isValidRead(String[] parts) {
        if (parts.length != 2) return false;

        if (!parts[1].matches("\\d+")) return false;
        if (Integer.parseInt(parts[1]) > 99) return false;
        if (Integer.parseInt(parts[1]) < 0) return false;

        return true;
    }

    private boolean isValidFullwrite(String[] parts) {
        // fullwrite 0xSSSSSSSS
        if (parts.length != 2) return false;

        if (!parts[1].startsWith("0x")) return false;
        if (parts[1].length() != 10) return false;
        if (!parts[1].substring(2).matches("[A-Z]+")) return false;

        return true;
    }

    private boolean isValidWrite(String[] parts) {
        // write 3 0xSSSSSSSS
        if (parts.length != 3) return false;

        if (!parts[1].matches("\\d+")) return false;
        if (Integer.parseInt(parts[1]) > 99) return false;
        if (Integer.parseInt(parts[1]) < 0) return false;

        if (!parts[2].startsWith("0x")) return false;
        if (parts[2].length() != 10) return false;
        if (!parts[2].substring(2).matches("[A-Z]+")) return false;

        return true;
    }
}

