package shell.processor;

import logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SSDRunner {
    private static final Logger logger = Logger.getInstance();

    public boolean run(String... args) {
        logger.printConsoleAndLog("SSDRunner.run()", "args = " + Arrays.toString(args));
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("ssd.jar");
        Collections.addAll(command, args);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            int exitCode = process.waitFor();

            logger.printConsoleAndLog("SSDRunner.run()", "action Passed");
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            logger.printConsoleAndLog("SSDRunner.run()", "action Failed");
            return false;
        }
    }
}