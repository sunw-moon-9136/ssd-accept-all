package SSD;

import java.util.List;

public interface InputHandler extends IOHandler {
    void add(String command);

    String read(int address);

    boolean isFull();

    List<String> flush();
}
