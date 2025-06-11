public abstract class DefaultTestScenario implements ITestScenario {
    protected final RunCommand runCommand;
    protected final Output output;

    public DefaultTestScenario(RunCommand runCommand, Output output) {
        this.runCommand = runCommand;
        this.output = output;
    }
}
