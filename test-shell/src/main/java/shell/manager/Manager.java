package shell.manager;

import logger.Logger;
import shell.Processor;
import shell.output.Output;

public class Manager implements IManager {
    private static final Logger logger = Logger.getInstance();

    Processor processor;
    Output output;

    public Manager(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
    }

    @Override
    public String read(int address) {
        logger.printConsoleAndLog("Manager.read()", "read RUN.");
        return runProcess("read " + address);
    }

    @Override
    public boolean write(int address, String value) {
        logger.printConsoleAndLog("Manager.write()", "write RUN.");
        return runProcess("write " + address + " " + value).equals("DONE");
    }

    @Override
    public boolean erase(int address, int size) {
        logger.printConsoleAndLog("Manager.erase()", "erase RUN.");
        return runProcess("erase " + address).equals("DONE");
    }

    @Override
    public boolean erase_range(int startLBA, int endLBA) {
        logger.printConsoleAndLog("Manager.erase_range()", "erase_range RUN.");
        return runProcess("erase_range " + startLBA + " " + endLBA).equals("DONE");
    }

    @Override
    public boolean flush() {
        logger.printConsoleAndLog("Manager.flush()", "flush RUN.");
        return runProcess("flush").equals("DONE");
    }

    private String runProcess(String command) {
        logger.printConsoleAndLog("Manager.runProcess()", "runProcess RUN.");
        if (processor.execute(command))
            return output.checkResult(command);
        return "ERROR";
    }
}
