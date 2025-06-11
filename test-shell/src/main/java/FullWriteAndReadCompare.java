public class FullWriteAndReadCompare extends DefaultTestScenario {

    private final Output output;

    public FullWriteAndReadCompare(RunCommand runCommand, Output output) {
        super(runCommand);
        this.output = output;
    }

    @Override
    public boolean run() {
        for (int i = 0; i <= 95; i += 5) {
            String testValue = makeHex(i / 5);

            // write
            for (int j = 0; j <= 4; j++) {
                if (!runCommand.execute(String.format("W %d %s", i + j, testValue)))
                    return false;
            }

            // read compare
            for (int j = 0; j <= 4; j++) {
                if (!runCommand.execute(String.format("R %d", i + j)))
                    return false;

                String result = output.checkResult("read").split(": ")[1];
                if (!result.equals(testValue))
                    return false;
            }
        }
        return true;
    }

    private String makeHex(int num) {
        return String.format("0x%02d%02d%02d%02d", num, num, num, num);
    }
}
