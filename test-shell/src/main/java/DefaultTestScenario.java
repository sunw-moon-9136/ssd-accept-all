public abstract class DefaultTestScenario implements ITestScenario {
    protected final RunCommand runCommand;

    public DefaultTestScenario(RunCommand runCommand) {
        this.runCommand = runCommand;
    }
}
