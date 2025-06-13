public interface NandDriver {
    String read(String file);

    void write(String file, byte[] bytes);
}
