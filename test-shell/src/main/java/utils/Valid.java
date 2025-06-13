package utils;

import java.util.Arrays;
import java.util.List;

public class Valid {
    public static final List<String> REGISTERED_COMMAND = Arrays.asList("read", "write", "fullread", "fullwrite", "help", "exit");
    public static final List<String> REGISTERED_SCENARIO_COMMAND = Arrays.asList(
            "1_FullWriteAndReadCompare", "2_PartialLBAWrite", "3_WriteReadAging", "4_EraseAndWriteAging",
            "1_", "2_", "3_", "4_");

    public static final List<String> ONE_LENGTH_COMMAND = Arrays.asList(
            "fullread", "help", "exit", "flush",
            "1_FullWriteAndReadCompare", "2_PartialLBAWrite", "3_WriteReadAging", "4_EraseAndWriteAging",
            "1_", "2_", "3_", "4_");
    public static final List<String> COMMAND_LBA = List.of("read");
    public static final List<String> COMMAND_DATA = List.of("fullwrite");
    public static final List<String> COMMAND_LBA_DATA = List.of("write");
    public static final List<String> COMMAND_LBA_SIZE = List.of("erase");
    public static final List<String> COMMAND_LBA_LBA = List.of("erase_range");

    public static boolean isNullEmpty(String command) {
        return command == null || command.isEmpty();
    }

    public static boolean isValidCommand(String[] parts) {
        if (checkRegisteredCommand(parts[0])) return true;
        if (ONE_LENGTH_COMMAND.contains(parts[0])) return isValidOneLength(parts);
        if (COMMAND_LBA.contains(parts[0])) return isValidCmdLBA(parts);
        if (COMMAND_DATA.contains(parts[0])) return isValidCmdData(parts);
        if (COMMAND_LBA_DATA.contains(parts[0])) return isValidCmdLBAData(parts);
        if (COMMAND_LBA_SIZE.contains(parts[0])) return isValidCmdLBASize(parts);
        if (COMMAND_LBA_LBA.contains(parts[0])) return isValidCmdLBALBA(parts);

        return false;
    }

    public static boolean checkRegisteredCommand(String command) {
        if (!(REGISTERED_COMMAND.contains(command) || REGISTERED_SCENARIO_COMMAND.contains(command))) return true;
        if (REGISTERED_COMMAND.contains(command) && !command.matches("[a-z]+")) return true;
        if (REGISTERED_SCENARIO_COMMAND.contains(command) && !command.matches("[a-z0-9_]+")) return true;

        return false;
    }

    public static boolean isValidOneLength(String[] parts) {
        if (parts.length != 1) return true;

        return false;
    }

    public static boolean isValidCmdLBA(String[] parts) {
        // COMMAND LBA (ex. read 3)
        if (parts.length != 2) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;

        return false;
    }

    public static boolean isValidCmdData(String[] parts) {
        // COMMAND DATA (ex. fullwrite 0xSSSSSSSS)
        if (parts.length != 2) return true;
        if (isValidDataArgument(parts[1])) return true;

        return false;
    }

    public static boolean isValidCmdLBAData(String[] parts) {
        // COMMAND LBA DATA (ex. write 3 0xSSSSSSSS)
        if (parts.length != 3) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;
        if (isValidDataArgument(parts[2])) return true;

        return false;
    }

    public static boolean isValidCmdLBASize(String[] parts) {
        // COMMAND LBA SIZE (ex. erase 3 100, erase 50 -10)
        if (parts.length != 3) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;
        if (isValidSizeArgument(parts[2])) return true;

        return false;
    }

    public static boolean isValidCmdLBALBA(String[] parts) {
        // COMMAND LBA LBA (ex. erase_range 4 10)
        if (parts.length != 3) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;
        if (isValidLBAPositionArgument(parts[2])) return true;

        return false;
    }

    private static boolean isValidLBAPositionArgument(String arg) {
        if (!arg.matches("\\d+")) return true;
        if (Integer.parseInt(arg) > 99) return true;
        if (Integer.parseInt(arg) < 0) return true;

        return false;
    }

    private static boolean isValidDataArgument(String arg) {
        if (!arg.startsWith("0x")) return true;
        if (arg.length() != 10) return true;
        if (!arg.substring(2).matches("[A-F0-9]+")) return true;

        return false;
    }

    private static boolean isValidSizeArgument(String arg) {
        if (!arg.matches("\\d+")) return true;

        return false;
    }
}
