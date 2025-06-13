package shell.processor.command;

import shell.processor.SSDRunner;

public class EraseRangeCommand implements Command {
    private static final int ERASE_UNIT_SIZE = 10;

    private final SSDRunner runner;

    public EraseRangeCommand() {
        this.runner = new SSDRunner();
    }

    public EraseRangeCommand(SSDRunner runner) {
        this.runner = runner;
    }

    @Override
    public boolean execute(String[] args) {
        int start = Math.min(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        int end = Math.max(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        for (int i = start; i <= end; i += ERASE_UNIT_SIZE) {
            int len = Math.min(ERASE_UNIT_SIZE, end - i + 1);
            if (!runner.run("E", String.valueOf(i), String.valueOf(len))) {
                return false;
            }
        }
        return true;
    }
}
