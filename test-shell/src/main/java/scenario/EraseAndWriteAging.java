package scenario;

import shell.Processor;
import shell.output.Output;
import utils.RandomFactory;

public class EraseAndWriteAging extends DefaultTestScenario {

    public EraseAndWriteAging(Processor processor, Output output) {
        super(processor, output);
    }

    public EraseAndWriteAging(Processor processor, Output output, RandomFactory randomFactory) {
        super(processor, output, randomFactory);
    }

    @Override
    public boolean run() {
        processor.execute("erase_range 0 2");

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 100; j += 2) {
                processor.execute(String.format("write %d %s", j, randomFactory.getRandomHexValue()));
                processor.execute(String.format("write %d %s", j, randomFactory.getRandomHexValue()));
                processor.execute(String.format("erase %d %d", j, j + 2));

                for (int k = 0; k <= 2; k++) {
                    if (!readCompare(k, "0x00000000"))
                        return false;
                }
            }
        }
        return true;
    }
}
