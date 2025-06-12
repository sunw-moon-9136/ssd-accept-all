package shell.manager;

import shell.Processor;
import shell.output.Output;

public class Manager implements IManager {
    Processor processor;
    Output output;

    public Manager(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
    }

    @Override
    public String read(int address) {
        return runProcess("read " + address);
    }

    @Override
    public boolean write(int address, String value) {
        runProcess("write " + address + " " + value);
        return false;
    }

    @Override
    public boolean erase(int address, int size) {
        return false;
    }

    @Override
    public boolean erase_range(int startLBA, int endLBA) {
        return false;
    }

    private String runProcess(String command) {
        String[] parts = command.split("\\s+");
        if (processor.execute(command)) return output.checkResult(parts[0], parts[1]);
        return "ERROR";
    }
}
