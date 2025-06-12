package scenario;

import shell.processor.Processor;
import shell.output.Output;
import utils.RandomFactory;

public class WriteReadAging extends DefaultTestScenario {

    RandomFactory randomFactory;

    public WriteReadAging(Processor processor, Output output) {
        super(processor, output);
        randomFactory = new RandomFactory();
    }

    public WriteReadAging(Processor processor, Output output, RandomFactory randomFactory) {
        super(processor, output);
        this.randomFactory = randomFactory;
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
