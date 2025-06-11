import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunCommand {
    class CommandNames {
        public static final String WRITE = "write";
        public static final String READ = "read";
        public static final String FULL_WRITE = "fullwrite";
        public static final String FULL_READ = "fullread";
        public static final String HELP = "help";
        public static final String EXIT = "exit";
    }

    public static final int MAX_LBA = 99;
    public static final int MIN_LBA = 0;
    private final Output output;

    public RunCommand(Output output) {
        this.output = output;
    }

    public void execute(String input) throws IOException, InterruptedException {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case CommandNames.WRITE -> write(parts);
            case CommandNames.READ -> read(parts);
            case CommandNames.FULL_WRITE -> fullWrite(parts);
            case CommandNames.FULL_READ -> fullRead();
            case CommandNames.HELP -> help();
            case CommandNames.EXIT -> exit();
            default -> throw new IllegalArgumentException("Unknown command");
        }
    }

    private void write(String[] parts) throws IOException, InterruptedException {
        runSSDCommand("W", parts[1], parts[2]);
        output.checkResult(CommandNames.WRITE);
    }

    private void read(String[] parts) throws IOException, InterruptedException {
        runSSDCommand("R", parts[1]);
        output.checkResult(CommandNames.READ);
    }

    private void fullWrite(String[] parts) throws IOException, InterruptedException {
        String value = parts[1];
        for (int lba = MIN_LBA; lba <= MAX_LBA; lba++) {
            runSSDCommand("W", String.valueOf(lba), value);
            output.checkResult(CommandNames.WRITE);
        }
    }

    private void fullRead() throws IOException, InterruptedException {
        for (int lba = MIN_LBA; lba <= MAX_LBA; lba++) {
            runSSDCommand("R", String.valueOf(lba));
            output.checkResult(CommandNames.READ);
        }
    }

    private void help() throws IOException, InterruptedException {

    }

    private void exit() throws IOException, InterruptedException {

    }

    void runSSDCommand(String... args) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("ssd.jar");
        Collections.addAll(command, args);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.start().waitFor();
    }
}

