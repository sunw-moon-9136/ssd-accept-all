package shell.processor;

import logger.Logger;
import shell.processor.command.*;
import java.util.Arrays;

public class Processor {
    private static final Logger logger = Logger.getInstance();

    public boolean execute(String input) {
        logger.printConsoleAndLog("Processor.execute()", "input is " + input);
        String[] parts = input.trim().split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = switch (commandName) {
            case "write" -> new WriteCommand();
            case "read" -> new ReadCommand();
            case "erase" -> new EraseCommand();
            case "erase_range" -> new EraseRangeCommand();
            case "flush" -> new FlushCommand();
            default -> throw new IllegalArgumentException("Unknown command: " + commandName);
        };

        return command.execute(args);
    }
}

