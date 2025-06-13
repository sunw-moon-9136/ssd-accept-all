package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class WriteReadAging extends DefaultTestScenario {

    private static ITestScenario testScenario;

    public static ITestScenario getInstance(IManager manager) {
        if (testScenario == null)
            testScenario = new WriteReadAging(manager);
        return testScenario;
    }

    public static ITestScenario getInstance(IManager manager, RandomFactory randomFactory) {
        if (testScenario == null)
            testScenario = new WriteReadAging(manager, randomFactory);
        return testScenario;
    }

    private WriteReadAging(IManager manager) {
        super(manager);
    }

    private WriteReadAging(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean run() {
        for (int i = 0; i < 200; i++) {
            String value = randomFactory.getRandomHexValue();
            if(!manager.write(0, value)) return false;
            if (!readCompare(0, value))
                return false;

            value = randomFactory.getRandomHexValue();
            if(!manager.write(99, value)) return false;
            if (!readCompare(99, value))
                return false;
        }
        return true;
    }
}
