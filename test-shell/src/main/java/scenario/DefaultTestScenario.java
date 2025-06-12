package scenario;

import shell.Processor;
import shell.output.Output;

public abstract class DefaultTestScenario implements ITestScenario {
    protected final Processor processor;
    protected final Output output;

    public DefaultTestScenario(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
    }

    protected boolean readCompare(int testAddress, String testValue) {
        if (!processor.execute(String.format("read %d", testAddress)))
            return false;

        String value = "0x" + output.checkResult("read", String.valueOf(testAddress))
                .split("0x")[1].trim();
        return value.equals(testValue);
    }
}
