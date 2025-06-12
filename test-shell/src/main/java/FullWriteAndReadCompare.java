public class FullWriteAndReadCompare extends DefaultTestScenario {

    public FullWriteAndReadCompare(RunCommand runCommand, Output output) {
        super(runCommand, output);
    }

    public FullWriteAndReadCompare(RunCommand runCommand, Output output, RandomFactory randomFactory) {
        super(runCommand, output, randomFactory);
    }

    @Override
    public boolean run() {
        for (int addressByFive = 0; addressByFive <= 95; addressByFive += 5) {
            String testValue = randomFactory.getRandomHexValue();

            if (!writeFiveValues(addressByFive, testValue)) return false;
            if (!readCompareFiveValues(addressByFive, testValue)) return false;
        }
        return true;
    }

    private boolean writeFiveValues(int baseAddress, String testValue) {
        for (int additionalAddress = 0; additionalAddress <= 4; additionalAddress++) {
            if (!runCommand.execute(String.format("write %d %s", baseAddress + additionalAddress, testValue)))
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
}
