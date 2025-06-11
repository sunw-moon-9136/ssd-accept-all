public class Main {
    public static void main(String[] args) {
        Driver driver = new FileDriver();
        ReadWritable ssd = new Ssd();
        ArgsParser parser = new ArgsParser(driver,ssd);
    }
}
