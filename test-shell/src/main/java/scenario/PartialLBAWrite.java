package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class PartialLBAWrite extends DefaultTestScenario {
    public final int repeatCnt = 30;
    public final String[] inputAddressList = {"4", "0", "3", "1", "2"};
    public static final String WRITE_EXPECT_VALUE = "0xAAAABBBB";

    private static ITestScenario testScenario;

    public static ITestScenario getInstance(IManager manager) {
        if (testScenario == null)
            testScenario = new PartialLBAWrite(manager);
        return testScenario;
    }

    private PartialLBAWrite(IManager manager) {
        super(manager);
    }

    // @VisibleForTesting
    PartialLBAWrite(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean runEach() {
        for (int i = 0; i < repeatCnt; i++) {
            logger.printConsoleAndLog("EraseAndWriteAging.runOnce()", "Loop(" + i + ") running");
            if (!runOnce()) return false;
        }
        return true;
    }

    private boolean runOnce() {
        try {
            for (String inputAddress : inputAddressList) {
                if (!manager.write(Integer.parseInt(inputAddress), WRITE_EXPECT_VALUE)) return false;
            }
            for (String inputAddress : inputAddressList) {
                if (!readCompare(Integer.parseInt(inputAddress), WRITE_EXPECT_VALUE)) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
