package shell.output;

import logger.Logger;

import java.util.Set;

public class Output {

    private static final Logger logger = Logger.getInstance();
    private static final String OUTPUT_FILE_PATH = "ssd_output.txt";

    private final DataReader dataReader;

    public Output() {
        this.dataReader = new OutputFileReader(OUTPUT_FILE_PATH);
    }

    public Output(DataReader dataReader) {
        this.dataReader = dataReader;
    }


    private static final String OUTPUT_RESULT_PASS = "DONE";
    private static final String OUTPUT_RESULT_ERROR = "ERROR";

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


    public String checkResult(String commandLine) {


        String command = getCommand(commandLine);
        String readResult;

        logger.printConsoleAndLog("Output.checkResult()", command + " ---- ");

        try {
            if (!existFileCheck()) {
                logger.printConsoleAndLog("Output.checkResult()", "output file not found");
                return OUTPUT_RESULT_ERROR;
            }

            readResult = readLine();

            logger.printConsoleAndLog("Output.checkResult()", "outputfile read");

            return switch (command) {
                case COMMAND_CHECK_OUTPUT_NULL -> checkOutput(readResult);
                case COMMAND_CHECK_OUTPUT_READ -> getReadOutput(readResult);
                default -> OUTPUT_RESULT_ERROR;
            };


        } catch (Exception e) {
            return OUTPUT_RESULT_ERROR;
        }

    }

    private String getReadOutput(String readResult) {
        if (readResult == null || readResult.isEmpty()) return OUTPUT_RESULT_ERROR;
        if (!readResult.contains("0x")) return OUTPUT_RESULT_ERROR;
        if (readResult.contains("ERROR")) return OUTPUT_RESULT_ERROR;
        return readResult;
    }

    private String checkOutput(String readResult) {
        if (readResult == null || readResult.isEmpty()) return OUTPUT_RESULT_PASS;
        if (readResult.contains("ERROR")) return OUTPUT_RESULT_ERROR;
        return OUTPUT_RESULT_ERROR;
    }
}