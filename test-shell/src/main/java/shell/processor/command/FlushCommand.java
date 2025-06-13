package shell.processor.command;

import shell.processor.SSDRunner;

public class FlushCommand implements Command {
    private final SSDRunner runner;

    public FlushCommand() {
        this.runner = new SSDRunner();
    }

    public FlushCommand(SSDRunner runner) {
        this.runner = runner;
    }

    @Override
    public boolean execute(String[] args) {
        return runner.run("F");
    }
}