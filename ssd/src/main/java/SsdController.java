public class SsdController {
    private Driver driver;
    private ReadWritable disk;

    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";

    public SsdController() {
        this.driver = new FileDriver();
        this.disk = new Ssd();
    }

    public SsdController(Driver driver, ReadWritable disk) {
        this.driver = driver;
        this.disk = disk;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setDisk(Ssd disk) {
        this.disk = disk;
    }

    public void error() {
        driver.write("ssd_output.txt", "ERROR".getBytes());
    }

    private boolean isValidLBA(String lba) {
        try {
            int number = Integer.parseInt(lba);
            return number >= 0 && number <= 99;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidValue(String value) {
        return value.matches("0x[A-F0-9]{8}$");
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

    private boolean isValidArgs(String[] args) {
        return isValidReadCommand(args) ||
                isValidWriteCommand(args);
    }

    private void write(int lba, String value) {
        disk.write(lba, value);
    }

    private void read(int lba) {
        String ret = disk.read(lba);
        driver.write(SSD_OUTPUT_TXT, ret.getBytes());
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();

            // cmd: ssd mode lba [value]
            String mode = args[0];
            int lba = Integer.parseInt(args[1]);

            if (mode.equals("R")) read(lba);
            if (mode.equals("W")) {
                String value = args[2];
                write(lba, value);
            }
        } catch (Exception e) {
            error();
        }
    }
}
