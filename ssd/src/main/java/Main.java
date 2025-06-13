public class Main {
    public static void main(String[] args) {
        SsdManager manager = new SsdManager();
        String[] tmps = {"E","3","3"};
//        String[] tmps = {"W","3","0x12345678"};
        manager.run(tmps);
    }
}
