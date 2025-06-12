public abstract class DefaultTestScenario implements ITestScenario {
    protected final RandomFactory randomFactory;
    protected final RunCommand runCommand;
    protected final Output output;

    public DefaultTestScenario(RunCommand runCommand, Output output) {
        this.runCommand = runCommand;
        this.output = output;
        this.randomFactory = new RandomFactory();
    }

    public DefaultTestScenario(RunCommand runCommand, Output output, RandomFactory randomFactory) {
        this.runCommand = runCommand;
        this.output = output;
        this.randomFactory = randomFactory;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        if (!runCommand.execute(String.format("read %d", testAddress)))
            return false;

        String value = "0x" + output.checkResult("read", String.valueOf(testAddress))
                .split("0x")[1].trim();
        return value.equals(testValue);
    }
}
