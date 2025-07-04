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

    private WriteReadAging(IManager manager) {
        super(manager);
    }

    // @VisibleForTesting
    WriteReadAging(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean runEach() {
        for (int i = 0; i < 200; i++) {
            logger.printConsoleAndLog("EraseAndWriteAging.runOnce()", "Loop(" + i + ") running");
            if (!runHalfWith(0)) return false;
            if (!runHalfWith(99)) return false;
        }
        return true;
    }

    private boolean runHalfWith(int address) {
        String value = randomFactory.getRandomHexValue();
        if(!manager.write(address, value)) return false;
        return readCompare(address, value);
    }
}
