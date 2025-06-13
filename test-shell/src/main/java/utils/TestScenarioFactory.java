package utils;

import scenario.*;
import shell.manager.IManager;

public class TestScenarioFactory {
    public static ITestScenario getTestScenario(String command, IManager manager) {
        if (command.equals("1_FullWriteAndReadCompare") || command.equals("1_")) {
            return FullWriteAndReadCompare.getInstance(manager);
        } else if (command.equals("2_PartialLBAWrite") || command.equals("2_")) {
            return PartialLBAWrite.getInstance(manager);
        } else if (command.equals("3_WriteReadAging") || command.equals("3_")) {
            return WriteReadAging.getInstance(manager);
        } else if (command.equals("4_EraseAndWriteAging") || command.equals("4_")) {
            return EraseAndWriteAging.getInstance(manager);
        }
        return null;
    }
}
