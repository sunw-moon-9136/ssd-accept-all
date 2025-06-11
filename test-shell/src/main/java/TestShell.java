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
        // read 3
        if (parts.length != 2) return false;
        if (isValidLBAPositionArgument(parts[1])) return false;

        return true;
    }

    private boolean isValidFullwrite(String[] parts) {
        // fullwrite 0xSSSSSSSS
        if (parts.length != 2) return false;
        if (isValidDataArgument(parts[1])) return false;

        return true;
    }

    private boolean isValidWrite(String[] parts) {
        // write 3 0xSSSSSSSS
        if (parts.length != 3) return false;
        if (isValidLBAPositionArgument(parts[1])) return false;
        if (isValidDataArgument(parts[2])) return false;

        return true;
    }

    private boolean isValidLBAPositionArgument(String arg) {
        if (!arg.matches("\\d+")) return true;
        if (Integer.parseInt(arg) > 99) return true;
        if (Integer.parseInt(arg) < 0) return true;

        return false;
    }

    private boolean isValidDataArgument(String arg) {
        if (!arg.startsWith("0x")) return true;
        if (arg.length() != 10) return true;
        if (!arg.substring(2).matches("[A-Z0-9]+")) return true;

        return false;
    }
}

