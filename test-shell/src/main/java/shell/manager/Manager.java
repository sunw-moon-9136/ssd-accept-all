package shell.manager;

import logger.Logger;
import shell.Processor;
import shell.output.Output;

public class Manager implements IManager {
    private static final Logger logger = Logger.getInstance();

    Processor processor;
    Output output;

    public Manager() {
        this.processor = new Processor();
        this.output = new Output();
    }

    // @VisibleForTesting
    public Manager(Processor processor, Output output) {
        this.processor = processor;
        this.output = output;
    }

    @Override
    public String read(int address) {
        logger.printConsoleAndLog("Manager.read()", "read RUN.");
        logger.printConsoleAndLog("Manager.read()", String.format("address: %d", address));
        return runProcess("read " + address);
    }

    @Override
    public boolean write(int address, String value) {
        logger.printConsoleAndLog("Manager.write()", "write RUN.");
        logger.printConsoleAndLog("Manager.write()", String.format("address: %d, value: %s", address, value));
        return runProcess("write " + address + " " + value).equals("DONE");
    }

    @Override
    public boolean erase(int address, int size) {
        logger.printConsoleAndLog("Manager.erase()", "erase RUN.");
        logger.printConsoleAndLog("Manager.erase()", String.format("address: %d, size: %d", address, size));
        return runProcess("erase " + address).equals("DONE");
    }

    @Override
    public boolean erase_range(int lba1, int lba2) {
        logger.printConsoleAndLog("Manager.erase_range()", "erase_range RUN.");
        logger.printConsoleAndLog("Manager.erase_range()", String.format("LBA_1: %d, LBA_2: %d", lba1, lba2));
        return runProcess("erase_range " + lba1 + " " + lba2).equals("DONE");
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
