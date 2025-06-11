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

    private boolean writeFiveValues(int i, String testValue) {
        for (int j = 0; j <= 4; j++) {
            if (!runCommand.execute(String.format("W %d %s", i + j, testValue)))
                return false;
        }
        return true;
    }

    private boolean readCompareFiveValues(int i, String testValue) {
        for (int j = 0; j <= 4; j++) {
            if (!runCommand.execute(String.format("R %d", i + j)))
                return false;

            String result = output.checkResult("read");
            String address = result.split(" : ")[0].substring(4);
            String value = result.split(" : ")[1];
            if (Integer.parseInt(address) != i + j)
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
