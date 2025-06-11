public class ReadOutputFactory {

    //TO-DO
    //private static final String OUTPUT_FILE_PATH = "ssd_output.txt";
    private static final String OUTPUT_FILE_PATH = "C:\\Users\\User\\Documents\\output.txt";


    public static Output readOutput() {
        DataReader realReader = new OutputFileReader(OUTPUT_FILE_PATH);
        return new Output(realReader);
    }
}