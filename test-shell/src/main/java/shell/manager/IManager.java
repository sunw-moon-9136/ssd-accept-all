package shell.manager;

public interface IManager {
    String read(int address);

    boolean write(int address, String value);

    boolean erase(int address, int size);

    boolean erase_range(int startLBA, int endLBA);

    boolean flush();
}
