package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class FullWriteAndReadCompare extends DefaultTestScenario {
    private static ITestScenario testScenario;

    public static ITestScenario getInstance(IManager manager) {
        if (testScenario == null)
            testScenario = new FullWriteAndReadCompare(manager);
        return testScenario;
    }

    private FullWriteAndReadCompare(IManager manager) {
        super(manager);
    }

    // @VisibleForTesting
    FullWriteAndReadCompare(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean run() {
        for (int addressByFive = 0; addressByFive <= 95; addressByFive += 5) {
            String testValue = randomFactory.getRandomHexValue();

            if (!writeFiveValues(addressByFive, testValue)) return false;
            if (!readCompareFiveValues(addressByFive, testValue)) return false;
        }
        return true;
    }

    private boolean writeFiveValues(int baseAddress, String testValue) {
        for (int additionalAddress = 0; additionalAddress <= 4; additionalAddress++) {
            if (!manager.write(baseAddress + additionalAddress, testValue))
                return false;
        }
        return true;
    }

    private boolean readCompareFiveValues(int baseAddress, String testValue) {
        for (int additionalAddress = 0; additionalAddress <= 4; additionalAddress++) {
            if (!readCompare(baseAddress + additionalAddress, testValue)) {
                return false;
            }
        }
        return true;
    }
}
