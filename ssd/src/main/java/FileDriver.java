public class FileDriver implements Driver {

    public static final String OUTPUT_FILE_NAME = "output.txt";
    public static final String NAND_FILE_NAME = "nand.txt";

    @Override
    public String read(String file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException();

        if (!file.equals(OUTPUT_FILE_NAME) && !file.equals(NAND_FILE_NAME))
            throw new IllegalArgumentException();

        return "";
    }

    @Override
    public void write(String file, byte[] bytes) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException();

        if (!file.equals(OUTPUT_FILE_NAME) && !file.equals(NAND_FILE_NAME))
            throw new IllegalArgumentException();
    }
}
