package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class EraseAndWriteAging extends DefaultTestScenario {
    private static ITestScenario testScenario;

    public static ITestScenario getInstance(IManager manager) {
        if (testScenario == null)
            testScenario = new EraseAndWriteAging(manager);
        return testScenario;
    }

    private EraseAndWriteAging(IManager manager) {
        super(manager);
    }

    // @VisibleForTesting
    EraseAndWriteAging(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean runEach() {
        if (init()) return false;

        for (int i = 0; i < 30; i++) {
            logger.printConsoleAndLog("EraseAndWriteAging.runOnce()", "Loop(" + i + ") running");
            if (!runOnce()) return false;
        }
        return true;
    }

    private boolean init() {
        return !manager.erase_range(0, 2);
    }

    private boolean runOnce() {
        for (int j = 2; j < 100; j += 2) {
            if (!manager.write(j, randomFactory.getRandomHexValue())) return false;
            if (!manager.write(j, randomFactory.getRandomHexValue())) return false;
            if (!manager.erase_range(j, Math.min(j + 2, 99))) return false;

            for (int k = 0; k <= 2 && k + j < 100; k++) {
                if (!readCompare(k + j, "0x00000000"))
                    return false;
            }
        }
        return true;
    }
}
