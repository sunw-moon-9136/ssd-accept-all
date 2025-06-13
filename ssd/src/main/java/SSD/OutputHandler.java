package SSD;

public interface OutputHandler extends IOHandler {
    String read(String file);

    void write(String file, byte[] bytes);
}
