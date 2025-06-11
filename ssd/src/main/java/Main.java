public class Main {
    public static void main(String[] args) {
        Driver driver = new FileDriver();
        ReadWritable ssd = new Ssd();
        SsdController parser = new SsdController(driver, ssd);
    }
}
