public class Ssd implements ReadWritable {
    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final String SSD_NAND_TXT = "ssd_nand.txt";

    private final Driver driver;
    private final String delimiter = "\t";

    public Ssd(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void read(int address) {

    }

    @Override
    public void write(int address, String value) {
        String inputString = address + delimiter + value;
        driver.write(SSD_NAND_TXT, inputString.getBytes());
    }
}
