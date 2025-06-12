package scenario;

import shell.processor.Processor;
import shell.output.Output;

public class FullWriteAndReadCompare extends DefaultTestScenario {

    public FullWriteAndReadCompare(Processor processor, Output output) {
        super(processor, output);
    }

    @Override
    public boolean run() {
        for (int addressByFive = 0; addressByFive <= 95; addressByFive += 5) {
            String testValue = makeHex(addressByFive / 5);

            if (!writeFiveValues(addressByFive, testValue)) return false;
            if (!readCompareFiveValues(addressByFive, testValue)) return false;
        }
        return true;
    }

    private boolean writeFiveValues(int baseAddress, String testValue) {
        for (int additionalAddress = 0; additionalAddress <= 4; additionalAddress++) {
            if (!processor.execute(String.format("write %d %s", baseAddress + additionalAddress, testValue)))
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

    private String makeHex(int num) {
        return String.format("0x%02d%02d%02d%02d", num, num, num, num);
    }
}
