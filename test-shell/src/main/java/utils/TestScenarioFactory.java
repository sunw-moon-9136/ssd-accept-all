package utils;

import scenario.*;
import shell.manager.IManager;

public class TestScenarioFactory {

    public static ITestScenario getTestScenario(String command, IManager manager) {
        return switch (command) {
            case "1_FullWriteAndReadCompare", "1_"
                    -> FullWriteAndReadCompare.getInstance(manager);
            case "2_PartialLBAWrite", "2_"
                    -> PartialLBAWrite.getInstance(manager);
            case "3_WriteReadAging", "3_"
                    -> WriteReadAging.getInstance(manager);
            case "4_EraseAndWriteAging", "4_"
                    -> EraseAndWriteAging.getInstance(manager);
            default -> null;
        };
    }
}
