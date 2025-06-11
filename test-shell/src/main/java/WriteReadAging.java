public class WriteReadAging extends DefaultTestScenario {

    RandomFactory randomFactory;

    public WriteReadAging(RunCommand runCommand, Output output) {
        super(runCommand, output);
        randomFactory = new RandomFactory();
    }

    public WriteReadAging(RunCommand runCommand, Output output, RandomFactory randomFactory) {
        super(runCommand, output);
        this.randomFactory = randomFactory;
    }

    @Override
    public boolean run() {
        for (int i = 0; i < 200; i++) {
            String value = randomFactory.getRandomHexValue();
            runCommand.execute("write 0 " + value);
            if (!readCompare(0, value))
                return false;

            value = randomFactory.getRandomHexValue();
            runCommand.execute("write 99 " + value);
            if (!readCompare(99, value))
                return false;
        }
        return true;
    }
}
