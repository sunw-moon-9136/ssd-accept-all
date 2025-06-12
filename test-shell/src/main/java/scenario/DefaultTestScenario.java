package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public abstract class DefaultTestScenario implements ITestScenario {
    protected final IManager manager;
    protected final RandomFactory randomFactory;

    public DefaultTestScenario(IManager manager) {
        this.manager = manager;
        this.randomFactory = new RandomFactory();
    }

    public DefaultTestScenario(IManager manager, RandomFactory randomFactory) {
        this.manager = manager;
        this.randomFactory = randomFactory;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        return getValueFromTestShell(testAddress).equals(testValue);
    }

    private String getValueFromTestShell(int testAddress) {
        return "0x" + manager.read(testAddress).split("0x")[1].trim();
    }
}
