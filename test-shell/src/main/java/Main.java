import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TestShell testShell = new TestShell();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (testShell.runTestShell(scanner).equals("exit")) break;
        }
        
        scanner.close();
    }
}
