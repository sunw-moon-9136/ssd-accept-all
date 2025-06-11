public class TestScenarioFactory {
    public static ITestScenario getTestScenario(String command, RunCommand runCommand, Output output) {
        if (command.equals("1_FullWriteAndReadCompare") || command.equals("1_")) {
            return new FullWriteAndReadCompare(runCommand, output);
        } else if (command.equals("2_PartialLBAWrite") || command.equals("2_")) {
            return new PartialLBAWrite(runCommand, output);
        } else if (command.equals("3_WriteReadAging") || command.equals("3_")) {
            return new WriteReadAging(runCommand, output);
        }
        return null;
    }
}
