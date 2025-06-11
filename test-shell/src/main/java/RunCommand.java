import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunCommand {
    private final Output output;


    public RunCommand(Output output) {
        this.output = output;
    }

    public void execute(String input) throws IOException, InterruptedException {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        if (command.equals("write")) runSSDCommand("W", parts[1], parts[2]);
        if (command.equals("read")) runSSDCommand("R", parts[1]);


        Output output;
        output = ReadOutputFactory.readOutput();
        output.checkResult(command);
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

