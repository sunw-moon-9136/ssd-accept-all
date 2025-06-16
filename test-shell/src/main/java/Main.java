import logger.Logger;
import scenario.ITestScenario;
import scenario.TestRunner;
import shell.manager.Manager;
import utils.Common;
import utils.TestScenarioFactory;
import utils.Valid;

import java.util.Scanner;

public class Main {
    private static final String INVALID_COMMAND = "INVALID COMMAND";

    private static final Logger logger = Logger.getInstance();
    public static Manager manager = new Manager();

    public static void main(String[] args) {
        logger.printConsoleAndLog("Main.main()", "================================");

        if (args.length == 1) {
            TestRunner testRunner = new TestRunner(args[0]);
            testRunner.process(manager);
            logger.printConsoleAndLog("Main.main()", "================================\n");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
        logger.printConsoleAndLog("Main.main()", "================================\n");
    }

    public static void run(Scanner scanner) {
        logger.printConsoleAndLog("Main.run()", "run RUN.");
        while (true) {
            System.out.print("Shell> ");
            String command = scanner.nextLine().trim();
            logger.printConsoleAndLog("Main.run()", String.format("[command: %s]", command));

            String[] parts = command.split("\\s+");
            if (Valid.isNullEmpty(command)) {
                logger.printConsoleAndLog("Main.run()", "The command is Null or Empty\n");
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (Valid.isValidCommand(parts)) {
                logger.printConsoleAndLog("Main.run()", "That command is not a valid command\n");
                System.out.println(INVALID_COMMAND);
                continue;
            }

            if (shellProcess(parts)) break;
            logger.printConsoleAndLog("Main.run()", "run complete!\n");
        }
    }

    public static boolean shellProcess(String[] parts) {
        logger.printConsoleAndLog("Main.shellProcess()", "shellProcess RUN.");
        return switch (parts[0]) {
            case "read" -> {
                readFormatPrint(parts[1], manager.read(Integer.parseInt(parts[1])));
                yield false;
            }

            case "write" -> {
                writeFormatPrint(manager.write(Integer.parseInt(parts[1]), parts[2]));
                yield false;
            }

            case "fullread" -> {
                for (int i = 0; i < 100; i++) {
                    readFormatPrint(String.format("%02d", i), manager.read(i));
                }
                yield false;
            }

            case "fullwrite" -> {
                for (int i = 0; i < 100; i++) {
                    writeFormatPrint(manager.write(i, parts[1]));
                }
                yield false;
            }

            case "erase" -> {
                isFormatPrint(manager.erase(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                yield false;
            }
            case "erase_range" -> {
                isFormatPrint(manager.erase_range(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                yield false;
            }
            case "flush" -> {
                isFormatPrint(manager.flush());
                yield false;
            }
            case "help" -> {
                Common.helpCommand();
                yield false;
            }
            case "exit" -> true;
            default -> {
                // SCENARIO_COMMAND
                ITestScenario testScenario = TestScenarioFactory.getTestScenario(parts[0], manager);
                if (testScenario != null) {
                    String result = testScenario.run() ? "PASS" : "FAIL";
                    System.out.println(result);
                }
                yield false;
            }
        };
    }

    private static void isFormatPrint(boolean ret) {
        if (ret) return;
        System.out.println("ERROR");
    }

    private static void writeFormatPrint(boolean write) {
        if (write) {
            System.out.println("[Write] DONE");
            return;
        }
        System.out.println("ERROR");
    }

    private static void readFormatPrint(String address, String read) {
        System.out.printf("[Read] %s %s\n", address, read);
    }

}
