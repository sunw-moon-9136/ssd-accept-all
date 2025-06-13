package shell.output;

public class Output {

    //TODO
    final static String OUTPUT_FILE_PATH = "ssd_output.txt";
    //final static String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";

    private final DataReader dataReader;

    public Output() {
        this.dataReader = new OutputFileReader(OUTPUT_FILE_PATH);
    }

    public Output(DataReader dataReader) {
        this.dataReader = dataReader;
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

        String readResult;
        try {
            if (!existFileCheck()) return "ERROR";
            readResult = readLine();
            if (commandLine.equals("write") || commandLine.equals("erase") || commandLine.equals("flush")) {
                if (readResult == null || readResult.isEmpty()) {
                    return "DONE";
                }
                if (readResult.contains("ERROR")) return "ERROR";
                return "ERROR";
            }

            if (commandLine.equals("read")) {
                if (readResult == null || readResult.isEmpty()) return "ERROR";
                if (!readResult.contains("0x")) return "ERROR";
                if (readResult.contains("ERROR")) return "ERROR";
                return readResult;
            }
        } catch (Exception e) {
            return "ERROR";
        }
        return "ERROR";
    }
}