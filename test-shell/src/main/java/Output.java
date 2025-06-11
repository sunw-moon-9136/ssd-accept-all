public class Output {

    //TODO
    final static String OUTPUT_FILE_PATH = "ssd_output.txt";
    // final static String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";

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


    public String checkResult(String commandLine) {
        String checkResult = "[" + commandLine + "] ";

        try {
            if (commandLine.equals("read")) {
                if (existFileCheck()) {
                    checkResult += "LBA ";
                    checkResult += readLine();
                    return checkResult;
                }
            }

            if (commandLine.equals("write")) {
                if (existFileCheck()) {

                    String readResult = readLine();
                    if (readResult == null || readResult.isEmpty()) {
                        checkResult += "DONE";
                        return checkResult;
                    }
                    return checkResult += "ERROR";

                }
            }
        } catch (Exception e) {
            return checkResult += "ERROR";
        }

        return checkResult += "ERROR";
    }
}