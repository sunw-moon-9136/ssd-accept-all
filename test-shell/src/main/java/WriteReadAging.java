public class WriteReadAging extends DefaultTestScenario {

    RandomFactory randomFactory;

    public WriteReadAging(RunCommand runCommand, Output output, RandomFactory randomFactory) {
        super(runCommand, output);
        this.randomFactory = randomFactory;
    }

    @Override
    public boolean run() {
        for (int i = 0; i < 200; i++) {
            String value = randomFactory.getRandomHexValue();
            runCommand.execute("W 00 " + value);
            if(!readCompare(0, value))
                return false;

            value = randomFactory.getRandomHexValue();
            runCommand.execute("W 99 " + value);
            if(!readCompare(99, value))
                return false;
        }

        return true;
    }

    private boolean readCompare(int testAddress, String testValue) {
        if (!runCommand.execute(String.format("R %d", testAddress)))
            return false;

        String result = output.checkResult("read");
        int address = Integer.parseInt(result.split(" : ")[0].substring(4));
        String value = result.split(" : ")[1];

        if (address != testAddress)
            return false;

        if (!value.equals(testValue))
            return false;

        return true;
    }
}
