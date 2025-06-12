package shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Processor {
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

        int start, end;

        if (size > 0) {
            start = lba;
            end = Math.min(lba + size - 1, 99);
        } else {
            start = Math.max(lba + size + 1, 0);
            end = lba;
        }

        for (int i = start; i <= end; i += 10) {
            int len = Math.min(10, end - i + 1);
            if (!runSSDCommand("E", String.valueOf(i), String.valueOf(len))) {
                return false;
            }
        }
        return true;
    }

    private boolean erase_range(String lba1, String lba2) {
        //erase_range [Start LBA] [End LBA]
        int start = Math.min(Integer.parseInt(lba1), Integer.parseInt(lba2));
        int end = Math.max(Integer.parseInt(lba1), Integer.parseInt(lba2));

        for (int i = start; i <= end; i += 10) {
            int len = Math.min(10, end - i + 1);
            if (!runSSDCommand("E", String.valueOf(i), String.valueOf(len))) {
                return false;
            }
        }
        return true;
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

