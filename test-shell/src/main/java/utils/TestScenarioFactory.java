package utils;

import scenario.*;
import shell.Processor;
import shell.manager.IManager;
import shell.output.Output;

public class TestScenarioFactory {
    public static ITestScenario getTestScenario(String command, IManager manager) {
        if (command.equals("1_FullWriteAndReadCompare") || command.equals("1_")) {
            return new FullWriteAndReadCompare(manager);
        } else if (command.equals("2_PartialLBAWrite") || command.equals("2_")) {
            return new PartialLBAWrite(manager);
        } else if (command.equals("3_WriteReadAging") || command.equals("3_")) {
            return new WriteReadAging(manager);
        } else if (command.equals("4_EraseAndWriteAging") || command.equals("4_")) {
            return new EraseAndWriteAging(manager);
        }
        return null;
    }
}
