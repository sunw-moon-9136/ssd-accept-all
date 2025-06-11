public abstract class DefaultTestScenario implements ITestScenario {
    protected final RunCommand runCommand;
    protected final Output output;

    public DefaultTestScenario(RunCommand runCommand, Output output) {
        this.runCommand = runCommand;
        this.output = output;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        if (!runCommand.execute(String.format("R %d", testAddress)))
            return false;

        String result = output.checkResult("read", "1");
        int address = Integer.parseInt(result.split(" : ")[0].substring(4));
        String value = result.split(" : ")[1];

        if (address != testAddress)
            return false;

        return value.equals(testValue);
    }
}
