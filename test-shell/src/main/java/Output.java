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


    public String checkResult(String commandLine) {
        String checkResult = "[" + commandLine + "] ";
        String readResult;
        try {
            if (!existFileCheck()) return checkResult += "ERROR";

            readResult = readLine();


            if (commandLine.equals("write")) {
                if (readResult == null || readResult.isEmpty()) {
                    checkResult += "DONE";
                    return checkResult;
                }

                if (readResult.contains("ERROR")) return checkResult += "ERROR";
                return checkResult += "ERROR";

            }

            if (commandLine.equals("read")) {
                if (!readResult.contains("0x")) return checkResult += "ERROR";
                if (readResult.contains("ERROR")) return checkResult += "ERROR";
                checkResult += "LBA ";
                checkResult += readResult;
                return checkResult;
            }


        } catch (Exception e) {
            return checkResult += "ERROR";
        }

        return checkResult += "ERROR";
    }
}