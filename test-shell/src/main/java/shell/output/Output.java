package shell.output;

import java.util.Set;

public class Output {


    private static final String OUTPUT_FILE_PATH = "ssd_output.txt";
    private final DataReader dataReader;

    public Output() {
        this.dataReader = new OutputFileReader(OUTPUT_FILE_PATH);
    }

    public Output(DataReader dataReader) {
        this.dataReader = dataReader;
    }


    private static final String COMMAND_CHECK_OUTPUT_NULL = "check_null";
    private static final String COMMAND_CHECK_OUTPUT_READ = "read";
    private static final Set<String> COMMANDS_CHECK_OUTPUT_NULL = Set.of(
            "write",
            "erase",
            "erase_range",
            "flush"
    );

    public String getCommand(String commandLine) {
        String[] commandLines = commandLine.split("\\s+");

        if (COMMANDS_CHECK_OUTPUT_NULL.contains(commandLines[0]))
            return COMMAND_CHECK_OUTPUT_NULL;

        return commandLines[0];
    }


    public boolean existFileCheck() {
        return dataReader.exists();
    }

    public String readLine() {
        return dataReader.readLine();
    }

    public String checkResult(String commandLine, String address) {
        return checkResult(commandLine);
    }

    public String checkResult(String commandLine) {

        String command = getCommand(commandLine);
        String readResult;

        try {
            if (!existFileCheck()) return "ERROR";

            readResult = readLine();

            if (command.equals(COMMAND_CHECK_OUTPUT_READ)) getReadOuput(readResult);
            if (command.equals(COMMAND_CHECK_OUTPUT_NULL)) checkOutputError(readResult);

        } catch (Exception e) {
            return "ERROR";
        }
        return "ERROR";
    }

    private String getReadOuput(String readResult) {
        if (readResult == null || readResult.isEmpty()) return "ERROR";
        if (!readResult.contains("0x")) return "ERROR";
        if (readResult.contains("ERROR")) return "ERROR";
        return readResult;
    }

    private String checkOutputError(String readResult) {
        if (readResult == null || readResult.isEmpty()) {
            return "DONE";
        }
        if (readResult.contains("ERROR")) return "ERROR";
        return "ERROR";
    }
}