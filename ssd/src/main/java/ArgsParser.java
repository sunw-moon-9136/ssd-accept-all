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

    public static boolean isValidModeCommand(String arg) {
        return (arg.equals("R") || arg.equals("W"));
    }

    public static boolean isValidArgs(String[] args) {
        return isValidModeCommand(args[0]);
    }

    public void run(String[] args) {
        try {
            if(!isValidArgs(args)) throw new IllegalArgumentException();
            if(args[0].equals("R")) {
                if(args.length != 2) throw new IllegalArgumentException();
            } else if(args[0].equals("W")) {

            }
        } catch(Exception e) {
            error();
        }
    }
}
