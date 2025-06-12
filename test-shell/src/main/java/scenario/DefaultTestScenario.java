package scenario;

import shell.Processor;
import shell.output.Output;
import utils.RandomFactory;

public abstract class DefaultTestScenario implements ITestScenario {
    protected final RandomFactory randomFactory;
    protected final Processor processor;
    protected final Output output;

    public DefaultTestScenario(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
        this.randomFactory = new RandomFactory();
    }

    public DefaultTestScenario(Processor processor, Output output, RandomFactory randomFactory) {
        this.processor = processor;
        this.output = output;
        this.randomFactory = randomFactory;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        if (!processor.execute(String.format("read %d", testAddress)))
            return false;

        String value = "0x" + output.checkResult("read", String.valueOf(testAddress))
                .split("0x")[1].trim();
        return value.equals(testValue);
    }
}
