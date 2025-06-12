package scenario;

import shell.manager.IManager;
import utils.RandomFactory;

public class WriteReadAging extends DefaultTestScenario {

    public WriteReadAging(IManager manager) {
        super(manager);
    }

    public WriteReadAging(IManager manager, RandomFactory randomFactory) {
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
