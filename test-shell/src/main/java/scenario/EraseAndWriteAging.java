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

    public static ITestScenario getInstance(IManager manager, RandomFactory randomFactory) {
        if (testScenario == null)
            testScenario = new EraseAndWriteAging(manager, randomFactory);
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
    public boolean run() {
        if (init()) return false;

        for (int i = 0; i < 30; i++) {
            if (!runOnce()) return false;
        }
        return true;
    }

    private boolean init() {
        return !manager.erase_range(0, 2);
    }

    private boolean runOnce() {
        for (int j = 0; j < 100; j += 2) {
            if (!manager.write(j, randomFactory.getRandomHexValue())) return false;
            if (!manager.write(j, randomFactory.getRandomHexValue())) return false;
            if (!manager.erase_range(j, j + 2)) return false;

            for (int k = 0; k <= 2; k++) {
                if (!readCompare(k, "0x00000000"))
                    return false;
            }
        }
        return true;
    }
}
