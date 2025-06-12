package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class EraseAndWriteAging extends DefaultTestScenario {

    public EraseAndWriteAging(IManager manager) {
        super(manager);
    }

    public EraseAndWriteAging(IManager manager, RandomFactory randomFactory) {
        super(manager, randomFactory);
    }

    @Override
    public boolean run() {
        if(!manager.erase_range(0, 2)) return false;

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 100; j += 2) {
                if(!manager.write(j, randomFactory.getRandomHexValue())) return false;
                if(!manager.write(j, randomFactory.getRandomHexValue())) return false;
                if(!manager.erase_range(j, j + 2)) return false;

                for (int k = 0; k <= 2; k++) {
                    if (!readCompare(k, "0x00000000"))
                        return false;
                }
            }
        }
        return true;
    }
}
