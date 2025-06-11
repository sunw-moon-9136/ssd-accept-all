public interface ReadWritable {
    String read(int address);

    void write(int address, String value);
}
