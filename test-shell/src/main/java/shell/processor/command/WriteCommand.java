package shell.processor.command;

import shell.processor.SSDRunner;

public class WriteCommand implements Command {
    private final SSDRunner runner;

    // 운영용 기본 생성자
    public WriteCommand() {
        this.runner = new SSDRunner();
    }

    // 테스트용 주입 생성자
    public WriteCommand(SSDRunner runner) {
        this.runner = runner;
    }

    @Override
    public boolean execute(String[] args) {
        return runner.run("W", args[0], args[1]);
    }
}
