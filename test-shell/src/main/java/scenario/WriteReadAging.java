package scenario;

import shell.Processor;
import shell.output.Output;
import utils.RandomFactory;

public class WriteReadAging extends DefaultTestScenario {

    public WriteReadAging(Processor processor, Output output) {
        super(processor, output);
    }

    public WriteReadAging(Processor processor, Output output, RandomFactory randomFactory) {
        super(processor, output, randomFactory);
    }

    @Override
    public boolean run() {
        for (int i = 0; i < 200; i++) {
            String value = randomFactory.getRandomHexValue();
            processor.execute("write 0 " + value);
            if (!readCompare(0, value))
                return false;

            value = randomFactory.getRandomHexValue();
            processor.execute("write 99 " + value);
            if (!readCompare(99, value))
                return false;
        }
        return true;
    }
}
