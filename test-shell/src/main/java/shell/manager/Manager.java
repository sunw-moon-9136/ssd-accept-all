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
        return runProcess("write " + address + " " + value).equals("DONE");
    }

    @Override
    public boolean erase(int address, int size) {
        return runProcess("erase " + address).equals("DONE");
    }

    @Override
    public boolean erase_range(int startLBA, int endLBA) {
        return runProcess("erase_range " + startLBA + " " + endLBA).equals("DONE");
    }

    @Override
    public boolean flush() {
        return runProcess("flush").equals("DONE");
    }

    private String runProcess(String command) {
        String[] parts = command.split("\\s+");
        if (processor.execute(command))
            return output.checkResult(parts[0], parts[1]);
        return "ERROR";
    }
}
