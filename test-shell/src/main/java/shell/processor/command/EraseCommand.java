package shell.processor.command;

import shell.processor.SSDRunner;

public class EraseCommand implements Command{
    private static final int MAX_LBA = 99;
    private static final int MIN_LBA = 0;
    private static final int ERASE_UNIT_SIZE = 10;
    private final SSDRunner runner;

    public EraseCommand() {
        this.runner = new SSDRunner();
    }

    public EraseCommand(SSDRunner runner) {
        this.runner = runner;
    }

    @Override
    public boolean execute(String[] args) {
        int lba = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);

        int start, end;

        if (size > 0) {
            start = lba;
            end = Math.min(lba + size - 1, MAX_LBA);
        } else {
            start = Math.max(lba + size + 1, MIN_LBA);
            end = lba;
        }

        for (int i = start; i <= end; i += ERASE_UNIT_SIZE) {
            int len = Math.min(ERASE_UNIT_SIZE, end - i + 1);
            if (!runner.run("E", String.valueOf(i), String.valueOf(len))) {
                return false;
            }
        }
        return true;
    }
}
