package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class PartialLBAWrite extends DefaultTestScenario {
    public final int repeatCnt = 30;
    public final String[] inputAddressList = {"4", "0", "3", "1", "2"};
    public static final String WRITE_EXPECT_VALUE = "0xAAAABBBB";

    public PartialLBAWrite(IManager manager) {
        super(manager);
    }

    public PartialLBAWrite(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean run() {
        for (int i = 0; i < repeatCnt; i++) {
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
