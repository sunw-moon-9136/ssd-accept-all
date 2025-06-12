package utils;

import scenario.FullWriteAndReadCompare;
import scenario.ITestScenario;
import scenario.PartialLBAWrite;
import scenario.WriteReadAging;
import shell.Processor;
import shell.output.Output;

public class TestScenarioFactory {
    public static ITestScenario getTestScenario(String command, Processor processor, Output output) {
        if (command.equals("1_FullWriteAndReadCompare") || command.equals("1_")) {
            return new FullWriteAndReadCompare(processor, output);
        } else if (command.equals("2_PartialLBAWrite") || command.equals("2_")) {
            return new PartialLBAWrite(processor, output);
        } else if (command.equals("3_WriteReadAging") || command.equals("3_")) {
            return new WriteReadAging(processor, output);
        }
        return null;
    }
}
