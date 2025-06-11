import java.io.File;

public class Ssd implements ReadWritable {
    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final String SSD_NAND_TXT = "ssd_nand.txt";
    public static final int MAX_ADDRESS_LENGTH = 100;

    private final String ADDRESS_VALUE_DELIMITER = "\t";
    private final String NEW_LINE_CHAR = "\n";
    private final String EMPTY_STRING = "";


    private Driver driver;

    public Ssd(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void read(int address) {
    }

    private void flushReadOutput() {
        driver.write(SSD_OUTPUT_TXT, "".getBytes());
    }

    private boolean isFileExist(String fileName) {
        return new File(fileName).exists();
    }

    @Override
    public void write(int address, String value) {
        if (!isFileExist(SSD_NAND_TXT)) {
            initializeNAND();
        }
        flushReadOutput();
        String writeContent = getWriteContent(address, value);
        driver.write(SSD_NAND_TXT, writeContent.getBytes());
    }

    private String getWriteContent(int address, String value) {
        String fileContent = driver.read(SSD_NAND_TXT);
        String[] contentLines = fileContent.split(NEW_LINE_CHAR);

        StringBuilder sb = new StringBuilder();
        for (String line : contentLines) {
            String writeString = "";
            String[] content = line.split(ADDRESS_VALUE_DELIMITER);
            String readAddress = content[0];
            String readValue = content[1];
            if (readAddress.equals(String.valueOf(address))) {
                writeString = readAddress + ADDRESS_VALUE_DELIMITER + value + NEW_LINE_CHAR;
            } else {
                writeString = readAddress + ADDRESS_VALUE_DELIMITER + readValue + NEW_LINE_CHAR;
            }
            sb.append(writeString);
        }
        return sb.toString();
    }

    private void initializeNAND() {
        StringBuilder sb = new StringBuilder();
        String initValue = "0x00000000";
        for (int i = 0; i < MAX_ADDRESS_LENGTH; i++) {
            String inputString = i + ADDRESS_VALUE_DELIMITER + initValue + NEW_LINE_CHAR;
            sb.append(inputString);
        }
        driver.write(SSD_NAND_TXT, sb.toString().getBytes());
    }
}
