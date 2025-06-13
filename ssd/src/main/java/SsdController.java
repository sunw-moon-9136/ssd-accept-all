import java.util.List;

public class SsdController {
    private Driver driver;
    private SsdOperator ssd;
    private SsdCommandBufferOptimizer buffer;

    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final byte[] ERROR_BYTES = "ERROR".getBytes();
    public static final byte[] EMPTY_BYTES = "".getBytes();

    public SsdController() {
        this.driver = new FileDriver();
        this.ssd = new DefaultSsd();
        this.buffer = new SsdCommandBufferOptimizer();
    }

    public SsdController(Driver driver, SsdOperator ssd) {
        this.driver = driver;
        this.ssd = ssd;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setSsd(DefaultSsd defaultSsd) {
        this.ssd = defaultSsd;
    }

    public void error() {
        driver.write(SSD_OUTPUT_TXT, ERROR_BYTES);
    }

    public void clear() {
        driver.write(SSD_OUTPUT_TXT, EMPTY_BYTES);
    }

    private boolean isValidLBA(String lba) {
        int number = Integer.parseInt(lba);
        return number >= 0 && number <= 99;
    }

    private boolean isValidValue(String value) {
        return value.matches("0x[A-F0-9]{8}$");
    }

    private boolean isValidEraseSize(int size) {
        return size >= 0 && size <= 10;
    }

    private boolean isValidReadCommand(String[] args) {
        return args.length == 2 &&
                args[0].equals("R") &&
                isValidLBA(args[1]);
    }

    private boolean isValidWriteCommand(String[] args) {
        return args.length == 3 &&
                args[0].equals("W") &&
                isValidLBA(args[1]) &&
                isValidValue(args[2]);
    }

    private boolean isValidEraseCommand(String[] args) {
        int maxLBA = Integer.parseInt(args[1]) + (args[2].equals("0") ? 0 : Integer.parseInt(args[2]) - 1);
        return args.length == 3 &&
                args[0].equals("E") &&
                isValidLBA(args[1]) &&
                isValidEraseSize(Integer.parseInt(args[2])) &&
                isValidLBA(String.valueOf(maxLBA));
    }

    private boolean isValidArgs(String[] args) {
        return isValidReadCommand(args) ||
                isValidWriteCommand(args) ||
                isValidEraseCommand(args);
    }

    private void write(int lba, String value) {
        if (buffer.isFull()) {
            flushBuffer();
        }
        buffer.add(String.join(" ", "W", String.valueOf(lba), value));

        clear();
    }

    private void read(int lba) {
        String ret = buffer.read(lba);
        if(ret.isBlank()) {
            ret = ssd.read(lba);
        }
        driver.write(SSD_OUTPUT_TXT, ret.getBytes());
    }

    private void erase(int lba, int size) {
        if (buffer.isFull()) {
            flushBuffer();
        }
        buffer.add(String.join(" ", "E", String.valueOf(lba), String.valueOf(size)));

        clear();
    }

    private void flushBuffer() {
        List<String> commands = buffer.flush();
        for (String cmd : commands) {
            String[] parts = cmd.split(" ");
            String mode = parts[0];
            int lba = Integer.parseInt(parts[1]);
            String value = parts[2];

            if (mode.equals("W")) {
                ssd.write(lba, value);
            } else if (mode.equals("E")) {
                ssd.erase(lba, Integer.parseInt(value));
            }
        }
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();

            // cmd: ssd mode lba [value]
            String mode = args[0];
            int lba = Integer.parseInt(args[1]);
            String value = args.length >= 3 ? args[2] : "";
            switch (mode) {
                case "R" ->{
                    read(lba);
                }
                case "W"->{
                    write(lba, value);
                }
                case "E"->{
                    erase(lba, Integer.parseInt(value));
                }
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            error();
        }
    }
}
