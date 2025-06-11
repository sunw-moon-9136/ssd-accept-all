import java.util.Scanner;

public class Main {
    public static RunCommand runCommand = new RunCommand();
    public static Output output = new Output(new OutputFileReader(""));

    public static void main(String[] args) {
        TestShell testShell = new TestShell(runCommand, output);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (testShell.runTestShell(scanner).equals("exit")) break;
        }

        scanner.close();
    }
}
