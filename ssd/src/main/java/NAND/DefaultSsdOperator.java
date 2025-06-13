package NAND;

public class DefaultSsdOperator extends AbstractSsdOperator {
    public static final int MAX_ADDRESS_LENGTH = 100;
    public static final String INIT_VALUE = "0x00000000";

    public static final String SSD_NAND_TXT = "ssd_nand.txt";

    private final String ADDRESS_VALUE_DELIMITER = "\t";
    private final String NEW_LINE_CHAR = "\n";

    public static class Builder extends AbstractSsdOperator.Builder<Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public DefaultSsdOperator build() {
            if (this.nandDriver == null) {
                this.nandDriver = new NandFileDriver(); // 기본값 설정
            }
            return new DefaultSsdOperator(this);
        }
    }

    private DefaultSsdOperator(Builder builder) {
        super(builder);
    }


    @Override
    public String read(int address) {
        if (!isFileExist(SSD_NAND_TXT)) initializeNand();
        String readValue = getAddressValue(address);
        return readValue;
    }

    private boolean isFileExist(String fileName) {
        try {
            nandDriver.read(fileName);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void initializeNand() {
        StringBuilder sb = new StringBuilder();
        for (int writeAddress = 0; writeAddress < MAX_ADDRESS_LENGTH; writeAddress++) {
            String writeString = generateWriteString(writeAddress, INIT_VALUE);
            sb.append(writeString);
        }
        nandDriver.write(SSD_NAND_TXT, sb.toString().getBytes());
    }


    private String getAddressValue(int readAddress) {
        String nandFullContents = nandDriver.read(SSD_NAND_TXT);

        for (String line : nandFullContents.split(NEW_LINE_CHAR)) {
            String[] content = line.split(ADDRESS_VALUE_DELIMITER);
            if (content[0].equals(String.valueOf(readAddress))) return content[1];
        }
        return "";
    }

    @Override
    public void write(int address, String value) {
        if (!isFileExist(SSD_NAND_TXT)) initializeNand();
        nandDriver.write(SSD_NAND_TXT, getWriteContent(address, value).getBytes());
    }

    @Override
    public void erase(int address, int size) {
        for (int i = 0; i < size; i++) {
            write(address + i, INIT_VALUE);
        }
    }

    private String getWriteContent(int writeAddress, String writeValue) {
        String fileContent = nandDriver.read(SSD_NAND_TXT);

        StringBuilder sb = new StringBuilder();
        for (String line : fileContent.split(NEW_LINE_CHAR)) {
            String[] readLineSplit = line.split(ADDRESS_VALUE_DELIMITER);
            int readAddress = Integer.parseInt(readLineSplit[0]);
            String readValue = readLineSplit[1];

            if (readAddress == writeAddress) readValue = writeValue;
            sb.append(generateWriteString(readAddress, readValue));
        }
        return sb.toString();
    }

    private String generateWriteString(int writeAddress, String writeValue) {
        return writeAddress + ADDRESS_VALUE_DELIMITER + writeValue + NEW_LINE_CHAR;
    }
}
