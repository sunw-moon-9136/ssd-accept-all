public class SsdController {
    private Driver driver;
    private ReadWritable disk;

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

    private boolean isValidReadCommand(String[] args) {
        return args.length == 2 &&
                args[0].equals("R") &&
                isValidLBA(args[1]);
    }

    private boolean isValidWriteCommand(String[] args) {
        return args.length == 3 &&
                args[0].equals("W") &&
                isValidLBA(args[1]);
    }

    private boolean isValidArgs(String[] args) {
        return isValidReadCommand(args) ||
                isValidWriteCommand(args);
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();
            if(args[0].equals("W") && args[2].length() != 10) throw new IllegalArgumentException();
        } catch (Exception e) {
            error();
        }
    }
}
