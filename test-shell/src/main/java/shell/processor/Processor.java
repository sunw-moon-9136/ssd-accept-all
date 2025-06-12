package shell.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Processor {
    public static final int MAX_LBS = 99;
    public static final int MIN_LBS = 0;

    private static class CommandNames {
        public static final String WRITE = "write";
        public static final String READ = "read";
        public static final String ERASE = "erase";
        public static final String ERASE_RANGE = "erase_range";
    }

    public boolean execute(String input) {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();
        String arg1 = "";
        String arg2 = "";

        if (parts.length == 3) {
            arg1 = parts[1];
            arg2 = parts[2];
            // 예: write 3 0xABCDFFFF / erase 0 10 / erase_range 6 26
        } else if (parts.length == 2) {
            arg1 = parts[1];
            // 예: read 3
        }

        return switch (command) {
            case CommandNames.WRITE -> write(arg1, arg2);
            case CommandNames.READ -> read(arg1);
            case CommandNames.ERASE -> erase(arg1, arg2);
            case CommandNames.ERASE_RANGE -> erase_range(arg1, arg2);
            default -> throw new IllegalArgumentException("Unknown command");
        };
    }

    private boolean write(String address, String value) {
        return runSSDCommand("W", address, value);
    }

    private boolean read(String address) {
        return runSSDCommand("R", address);
    }

    private boolean erase(String address, String eraseSize) {
        //erase [LBA] [SIZE]
        int lba = Integer.parseInt(address);
        int size = Integer.parseInt(eraseSize);

        if (size > 0) {
            int end = Math.min(MAX_LBS, lba + size - 1);
            int actualSize = end - lba + 1;

            for (int i = 0; i < actualSize; i += 10) {
                int chunkStart = lba + i;
                int chunkSize = Math.min(10, actualSize - i);

                if (!runSSDCommand("E", String.valueOf(chunkStart), String.valueOf(chunkSize))) {
                    return false;
                }
            }
        } else if (size < 0) {
            int absSize = Math.abs(size);
            int start = Math.max(MIN_LBS, lba - absSize + 1);
            int actualSize = lba - start + 1;

            for (int i = 0; i < actualSize; i += 10) {
                int chunkStart = start + i;
                int chunkSize = Math.min(10, actualSize - i);

                if (!runSSDCommand("E", String.valueOf(chunkStart), String.valueOf(chunkSize))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean erase_range(String arg1, String arg2) {
        //erase_range [Start LBA] [End LBA]
        int start = Math.min(Integer.parseInt(arg1), Integer.parseInt(arg2));
        int end = Math.max(Integer.parseInt(arg1), Integer.parseInt(arg2));

        if (start < MIN_LBS) start = MIN_LBS;
        if (end > MAX_LBS) end = MAX_LBS;

        return erase(String.valueOf(start), String.valueOf(end));
    }

    boolean runSSDCommand(String... args) {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("ssd.jar");
        Collections.addAll(command, args);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}

