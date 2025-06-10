public interface Driver {

    String read(String file);

    void write(String file, byte[] bytes);
}
