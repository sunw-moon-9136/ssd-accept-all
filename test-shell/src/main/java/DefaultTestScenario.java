public abstract class DefaultTestScenario implements ITestScenario {
    protected final RunCommand runCommand;
    protected final Output output;

    public DefaultTestScenario(RunCommand runCommand, Output output) {
        this.runCommand = runCommand;
        this.output = output;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        if (!runCommand.execute(String.format("read %d", testAddress)))
            return false;

        String value = "0x" + output.checkResult("read").split("0x")[1].trim();
        return value.equals(testValue);
    }
}
