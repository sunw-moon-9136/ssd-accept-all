public class EraseAndWriteAging extends DefaultTestScenario {

    public EraseAndWriteAging(RunCommand runCommand, Output output) {
        super(runCommand, output);
    }

    public EraseAndWriteAging(RunCommand runCommand, Output output, RandomFactory randomFactory) {
        super(runCommand, output, randomFactory);
    }

    @Override
    public boolean run() {
        runCommand.execute("erase_range 0 2");

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 100; j += 2) {
                runCommand.execute(String.format("write %d %s", j, randomFactory.getRandomHexValue()));
                runCommand.execute(String.format("write %d %s", j, randomFactory.getRandomHexValue()));
                runCommand.execute(String.format("erase %d %d", j, j + 2));

                for (int k = 0; k <= 2; k++) {
                    if (!readCompare(k, "0x00000000"))
                        return false;
                }
            }
        }
        return true;
    }
}
