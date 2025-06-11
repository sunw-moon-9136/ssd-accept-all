import java.util.Scanner;

public class Main {
    public static RunCommand runCommand = new RunCommand();
    public static Output output = new Output();

    public static void main(String[] args) {
        TestShell testShell = new TestShell(runCommand, output);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String result = testShell.runTestShell(scanner);
            if (result.equals("exit")) break;
            System.out.println(result);
        }

        scanner.close();
    }
}
