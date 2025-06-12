import shell.processor.Processor;
import shell.manager.Manager;
import shell.output.Output;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static final String INVALID_COMMAND = "INVALID COMMAND";
    public static final String[] REGISTERED_COMMAND = new String[]{"read", "write", "fullread", "fullwrite", "help", "exit"};
    public static final String[] ONE_LENGTH_COMMAND = new String[]{"fullread", "help", "exit", "1_FullWriteAndReadCompare", "2_PartialLBAWrite", "3_WriteReadAging", "1_", "2_", "3_"};
    public static final String[] COMMAND_LBA = new String[]{"read"};
    public static final String[] COMMAND_DATA = new String[]{"fullwrite"};
    public static final String[] COMMAND_LBA_DATA = new String[]{"write"};


    public static Processor processor = new Processor();
    public static Output output = new Output();

    public static void main(String[] args) {
        Manager manager = new Manager(processor, output);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Shell> ");
            String command = scanner.nextLine().trim();

            String[] parts = command.split("\\s+");
            if (isNullEmpty(command)) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (isValidCommand(parts)) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (manager.runTestShell(scanner).equals("exit")) break;
        }

        scanner.close();
    }

    private static boolean isNullEmpty(String command) {
        return command == null || command.isEmpty();
    }

    private static boolean isValidCommand(String[] parts) {
        if (checkRegisteredCommand(parts[0])) return true;
        if (Arrays.asList(ONE_LENGTH_COMMAND).contains(parts[0])) return isValidOneLength(parts);
        if (Arrays.asList(COMMAND_LBA).contains(parts[0])) return isValidCmdLBA(parts);
        if (Arrays.asList(COMMAND_DATA).contains(parts[0])) return isValidCmdData(parts);
        if (Arrays.asList(COMMAND_LBA_DATA).contains(parts[0])) return isValidCmdLBAData(parts);

        return false;
    }

    private static boolean checkRegisteredCommand(String command) {
        if (!Arrays.asList(REGISTERED_COMMAND).contains(command)) return true;
        if (!command.matches("[a-z]+")) return true;

        return false;
    }

    private static boolean isValidOneLength(String[] parts) {
        if (parts.length != 1) return true;

        return false;
    }

    private static boolean isValidCmdLBA(String[] parts) {
        // COMMAND LBA (ex. read 3)
        if (parts.length != 2) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;

        return false;
    }

    private static boolean isValidCmdData(String[] parts) {
        // COMMAND DATA (ex. fullwrite 0xSSSSSSSS)
        if (parts.length != 2) return true;
        if (isValidDataArgument(parts[1])) return true;

        return false;
    }

    private static boolean isValidCmdLBAData(String[] parts) {
        // COMMAND LBA DATA (ex. write 3 0xSSSSSSSS)
        if (parts.length != 3) return true;
        if (isValidLBAPositionArgument(parts[1])) return true;
        if (isValidDataArgument(parts[2])) return true;

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
}
