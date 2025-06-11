import java.util.Arrays;
import java.util.Scanner;

public class TestShell {
    public static final String INVALID_COMMAND = "INVALID COMMAND";
    public static final String[] REGISTERED_COMMAND = new String[]{"read", "write", "fullread", "fullwrite", "help", "exit"};
    public static final String[] ONE_LENGTH_COMMAND = new String[]{"fullread", "help", "exit"};
    public static final String[] COMMAND_LBA = new String[]{"read"};
    public static final String[] COMMAND_DATA = new String[]{"fullwrite"};
    public static final String[] COMMAND_LBA_DATA = new String[]{"write"};

    public String runTestShell(Scanner input) {
        System.out.print(">> ");
        String command = input.nextLine().trim();

        if (isNullEmpty(command)) return INVALID_COMMAND;

        String[] parts = command.split("\\s+");
        if (isValidCommand(parts)) return INVALID_COMMAND;

        if (parts[0].equals("help")) {
            System.out.println(Common.HELP_TEXT);
            return "help";
        }
        if (parts[0].equals("exit")) return "exit";

        return parts[0];
    }

    private boolean isNullEmpty(String command) {
        return command == null || command.isEmpty();
    }

    private boolean isValidCommand(String[] parts) {
        if (checkRegisteredCommand(parts[0])) return true;
        if (Arrays.asList(ONE_LENGTH_COMMAND).contains(parts[0])) return isValidOneLength(parts);
        if (Arrays.asList(COMMAND_LBA).contains(parts[0])) return isValidCmdLBA(parts);
        if (Arrays.asList(COMMAND_DATA).contains(parts[0])) return isValidCmdData(parts);
        if (Arrays.asList(COMMAND_LBA_DATA).contains(parts[0])) return isValidCmdLBAData(parts);

        return false;
    }

    private boolean checkRegisteredCommand(String command) {
        if (!Arrays.asList(REGISTERED_COMMAND).contains(command)) return true;
        if (!command.matches("[a-z]+")) return true;

        return false;
    }

    private boolean isValidOneLength(String[] parts) {
        if (parts.length != 1) return true;

        return false;
    }

    private boolean isValidCmdLBA(String[] parts) {
        // COMMAND LBA (ex. read 3)
        if (parts.length != 2) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;

        return false;
    }

    private boolean isValidCmdData(String[] parts) {
        // COMMAND DATA (ex. fullwrite 0xSSSSSSSS)
        if (parts.length != 2) return true;
        if (isValidDataArgument(parts[1])) return true;

        return false;
    }

    private boolean isValidCmdLBAData(String[] parts) {
        // COMMAND LBA DATA (ex. write 3 0xSSSSSSSS)
        if (parts.length != 3) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;
        if (isValidDataArgument(parts[2])) return true;

        return false;
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
        if (!arg.substring(2).matches("[A-F0-9]+")) return true;

        return false;
    }
}

