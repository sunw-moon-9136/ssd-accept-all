public interface SsdOperator {
    String read(int address);

    void write(int address, String value);

    void erase(int address, int size);
}
