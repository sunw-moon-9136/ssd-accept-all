public class Output {

    //TODO
    //final static String OUTPUT_FILE_PATH = "ssd_output.txt";
    final static String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";

    private final DataReader dataReader;

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
        String checkResult;
        if (commandLine.equals("read")) {
            if (existFileCheck()) {
                checkResult = readLine();
                System.out.println(checkResult);
                return checkResult;
            }
        }

        if (commandLine.equals("write")) {
            if (existFileCheck()) {

                checkResult = readLine();
                if (checkResult == null || checkResult.isEmpty() || checkResult.isBlank()) {
                    return "DONE";
                }
                return "FAIL";

            }
        }

        System.out.println("[" + commandLine + "]");
        return commandLine;
    }

}
