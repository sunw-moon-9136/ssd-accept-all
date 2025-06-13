package shell.processor.command;

import shell.processor.SSDRunner;

public class ReadCommand implements Command {
    private final SSDRunner runner;

    public ReadCommand() {
        this.runner = new SSDRunner();
    }

    public ReadCommand(SSDRunner runner) {
        this.runner = runner;
    }

    @Override
    public boolean execute(String[] args) {
        return runner.run("R", args[0]);
    }
}