public class Ssd implements ReadWritable {
    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final String SSD_NAND_TXT = "ssd_nand.txt";

    private final String ADDRESS_VALUE_DELIMITER = "\t";
    private final String EMPTY_STRING = "";

    private Driver driver;

    public Ssd(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void read(int address) {

    }

    private void flushOutput() {
        driver.write(SSD_OUTPUT_TXT, "".getBytes());
    }

    @Override
    public void write(int address, String value) {
        flushOutput();
        String inputString = address + ADDRESS_VALUE_DELIMITER + value;
        driver.write(SSD_NAND_TXT, inputString.getBytes());
    }
}
