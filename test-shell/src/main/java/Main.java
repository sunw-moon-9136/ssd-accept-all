import shell.Processor;
import shell.manager.Manager;
import shell.output.Output;
import utils.Common;
import utils.Valid;

import java.util.Scanner;

public class Main {
    private static final String INVALID_COMMAND = "INVALID COMMAND";

    private static Processor processor = new Processor();
    private static Output output = new Output();
    public static Manager manager = new Manager(processor, output);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        while (true) {
            System.out.print("Shell> ");
            String command = scanner.nextLine().trim();

            String[] parts = command.split("\\s+");
            if (Valid.isNullEmpty(command)) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (Valid.isValidCommand(parts)) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (shellProcess(parts)) break;
        }
    }

    public static boolean shellProcess(String[] parts) {
        boolean exitFlag = false;

        switch (parts[0]) {
            case "read":
                System.out.println(manager.read(Integer.parseInt(parts[1])));
                break;

            case "write":
                System.out.println(manager.write(Integer.parseInt(parts[1]), parts[2]));
                break;

            case "fullread":
                for (int i = 0; i < 100; i++) {
                    System.out.println(manager.read(i));
                }
                break;

            case "fullwrite":
                for (int i = 0; i < 100; i++) {
                    System.out.println(manager.write(i, parts[1]));
                }
                break;

            case "erase":
                manager.erase(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                break;

            case "erase_range":
                manager.erase_range(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                break;

            case "help":
                System.out.println(Common.HELP_TEXT);
                break;

            case "exit":
                exitFlag = true;
                break;

            default:
                break;
        }
        return exitFlag;
    }
}
