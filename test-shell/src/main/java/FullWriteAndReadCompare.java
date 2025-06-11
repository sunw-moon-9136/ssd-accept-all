public class FullWriteAndReadCompare extends DefaultTestScenario {

    public FullWriteAndReadCompare(RunCommand runCommand, Output output) {
        super(runCommand, output);
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
            if (!runCommand.execute(String.format("W %d %s", baseAddress + additionalAddress, testValue)))
                return false;
        }
        return true;
    }

    private boolean readCompareFiveValues(int baseAddress, String testValue) {
        for (int additionalAddress = 0; additionalAddress <= 4; additionalAddress++) {
            if (!runCommand.execute(String.format("R %d", baseAddress + additionalAddress)))
                return false;

            String result = output.checkResult("read");
            int address = Integer.parseInt(result.split(" : ")[0].substring(4));
            String value = result.split(" : ")[1];

            if (address != baseAddress + additionalAddress)
                return false;

            if (!value.equals(testValue))
                return false;
        }
        return true;
    }

    private String makeHex(int num) {
        return String.format("0x%02d%02d%02d%02d", num, num, num, num);
    }
}
