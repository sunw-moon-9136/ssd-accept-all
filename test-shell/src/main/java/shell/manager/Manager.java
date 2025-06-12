package shell.manager;

import scenario.ITestScenario;
import shell.processor.Processor;
import shell.output.Output;
import utils.Common;
import utils.TestScenarioFactory;

import java.util.Scanner;

public class Manager implements IManager {
    Processor processor;
    Output output;

    public Manager(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
    }

    @Override
    public String read(int address) {
        return "";
    }

    @Override
    public boolean write(int address, String value) {
        return false;
    }

    @Override
    public boolean fullread() {
        return false;
    }

    @Override
    public boolean fullwrite(String value) {
        return false;
    }

    @Override
    public boolean erase(int address, int size) {
        return false;
    }

    @Override
    public boolean erase_range(int startLBA, int endLBA) {
        return false;
    }

    public String runTestShell(Scanner input) {
        System.out.print("Shell> ");
        String command = input.nextLine().trim();
        String[] parts = command.split("\\s+");

        // Test Scenario
        ITestScenario testScenario = TestScenarioFactory.getTestScenario(parts[0], processor, output);
        if (testScenario != null) {
            String result = testScenario.run() ? "PASS" : "FAIL";
            System.out.println(result);
            return result;
        }

        if (parts[0].equals("help")) {
            System.out.println(Common.HELP_TEXT);
            return "help";
        }
        if (parts[0].equals("exit")) return "exit";

        // fullread, fullwrite
        String changeCommand = "";
        if (parts[0].equals("fullread")) {
            for (int i = 0; i < 100; i++) {
                changeCommand = parts[0].substring(4) + " " + i;
                printResult(runProcess(changeCommand));
            }
        } else if (parts[0].equals("fullwrite")) {
            for (int i = 0; i < 100; i++) {
                changeCommand = parts[0].substring(4) + " " + i + " " + parts[1];
                printResult(runProcess(changeCommand));
            }
        } else {
            //read, write
            printResult(runProcess(command));
        }

        return "\n";
    }

    void printResult(String str) {
        System.out.println(str);
    }


    private String runProcess(String command) {
        String[] parts = command.split("\\s+");
        if (processor.execute(command)) return output.checkResult(parts[0], parts[1]);
        return "ERROR";
    }
}
