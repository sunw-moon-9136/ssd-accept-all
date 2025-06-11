import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunCommand {
    private static class CommandNames {
        public static final String WRITE = "write";
        public static final String READ = "read";
    }

    public boolean  execute(String input) throws IOException, InterruptedException {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        return switch (command) {
            case CommandNames.WRITE -> write(parts);
            case CommandNames.READ -> read(parts);
            default -> throw new IllegalArgumentException("Unknown command");
        };
    }

    private boolean write(String[] parts) throws IOException, InterruptedException {
        return runSSDCommand("W", parts[1], parts[2]);
    }

    private boolean read(String[] parts) throws IOException, InterruptedException {
        return runSSDCommand("R", parts[1]);
    }

    boolean runSSDCommand(String... args) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("ssd.jar");
        Collections.addAll(command, args);

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        int exitCode = process.waitFor();

        return exitCode == 0;
    }
}

