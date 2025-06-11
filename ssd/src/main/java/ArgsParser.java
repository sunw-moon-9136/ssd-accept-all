public class ArgsParser {
    private Driver driver;
    private ReadWritable disk;

    public ArgsParser() {
        this.driver = new FileDriver();
        this.disk = new Ssd();
    }

    public ArgsParser(Driver driver, ReadWritable disk) {
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

    public static boolean isValidReadCommand(String[] args) {
        return args.length == 2 &&
                args[0].equals("R");
    }

    public static boolean isValidWriteCommand(String[] args) {
        return args.length == 3 &&
                args[0].equals("W");
    }

    public static boolean isValidArgs(String[] args) {
        return isValidReadCommand(args) ||
                isValidWriteCommand(args);
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();
            if (args[0].equals("R")) {
                if (Integer.parseInt(args[1]) >= 0 && Integer.parseInt(args[1]) <= 99) {

                } else {
                    throw new IllegalArgumentException();
                }
            } else if (args[0].equals("W")) {
                if (Integer.parseInt(args[1]) >= 0 && Integer.parseInt(args[1]) <= 99) {

                } else {
                    throw new IllegalArgumentException();
                }
            }
        } catch (Exception e) {
            error();
        }
    }
}
