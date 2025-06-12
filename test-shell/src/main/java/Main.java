import scenario.ITestScenario;
import scenario.TestRunner;
import shell.Processor;
import shell.manager.Manager;
import shell.output.Output;
import utils.Common;
import utils.TestScenarioFactory;
import utils.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String INVALID_COMMAND = "INVALID COMMAND";
    public static final List<String> SCENARIO_COMMAND = Arrays.asList("1_FullWriteAndReadCompare", "2_PartialLBAWrite", "3_WriteReadAging", "4_EraseAndWriteAging", "1_", "2_", "3_", "4_");

    private static Processor processor = new Processor();
    private static Output output = new Output();
    public static Manager manager = new Manager(processor, output);

    public static void main(String[] args) {

        if (args[0].length() > 0) {
            TestRunner testRunner = new TestRunner(args[0]);
            testRunner.process(manager);
            return;
        }

        Manager manager = new Manager(processor, output);
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
        if (SCENARIO_COMMAND.contains(parts[0])) {
            ITestScenario testScenario = TestScenarioFactory.getTestScenario(parts[0], manager);
            if (testScenario != null) {
                String result = testScenario.run() ? "PASS" : "FAIL";
                System.out.println(result);
            }
            return false;
        }

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

            case "help" -> Common.helpCommand();
            case "erase" -> manager.erase(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            case "erase_range" -> manager.erase_range(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            case "exit" -> true;
            default -> false;
        };
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
