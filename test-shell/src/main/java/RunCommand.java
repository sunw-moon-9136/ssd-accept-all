import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunCommand {

    public void write(String s) throws IOException, InterruptedException {
        String[] str = s.split("\\s+");
        String lba = str[1];
        String value = str[2];

        runSSDCommand("W", lba, value);
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

