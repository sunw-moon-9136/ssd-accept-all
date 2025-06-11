import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TestShell testShell = new TestShell();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String ret = testShell.runTestShell(scanner);
            System.out.println("[" + ret + "]");
            if (ret.equals("exit")) break;
        }
        scanner.close();
    }
}
