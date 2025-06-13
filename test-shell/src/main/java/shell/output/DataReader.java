package shell.output;

public interface DataReader {
    boolean exists();

    String readLine();
}