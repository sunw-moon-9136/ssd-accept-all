public interface ReadWritable {
    void read(int address);

    void write(int address, String value);
}
