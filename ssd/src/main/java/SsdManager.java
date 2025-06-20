import NAND.ReadWritable;
import SSD.InputHandler;
import SSD.OutputHandler;

import java.util.List;

public class SsdManager {
    private ReadWritable ssd;
    private InputHandler inputHandler;
    private OutputHandler outputHandler;

    private SsdManager(SsdManagerBuilder builder) {
        this.ssd = builder.ssd;
        this.inputHandler = builder.inputHandler;
        this.outputHandler = builder.outputHandler;
    }

    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final byte[] ERROR_BYTES = "ERROR".getBytes();
    public static final byte[] EMPTY_BYTES = "".getBytes();

    public void error() {
        outputHandler.write(SSD_OUTPUT_TXT, ERROR_BYTES);
    }

    public void clear() {
        outputHandler.write(SSD_OUTPUT_TXT, EMPTY_BYTES);
    }

    private boolean isValidLBA(String lba) {
        int number = Integer.parseInt(lba);
        return number >= 0 && number <= 99;
    }

    private boolean isValidValue(String value) {
        return value.matches("0x[A-F0-9]{8}$");
    }

    private boolean isValidEraseSize(int size) {
        return size >= 0 && size <= 10;
    }

    private boolean isValidReadCommand(String[] args) {
        return args.length == 2 &&
                args[0].equals("R") &&
                isValidLBA(args[1]);
    }

    private boolean isValidWriteCommand(String[] args) {
        return args.length == 3 &&
                args[0].equals("W") &&
                isValidLBA(args[1]) &&
                isValidValue(args[2]);
    }

    private boolean isValidEraseCommand(String[] args) {
        if(args.length != 3) return false;
        int maxLBA = Integer.parseInt(args[1]) + (args[2].equals("0") ? 0 : Integer.parseInt(args[2]) - 1);
        return args.length == 3 &&
                args[0].equals("E") &&
                isValidLBA(args[1]) &&
                isValidEraseSize(Integer.parseInt(args[2])) &&
                isValidLBA(String.valueOf(maxLBA));
    }

    private boolean isValidFlushCommand(String[] args) {
        return args.length == 1 && args[0].equals("F");
    }

    private boolean isValidArgs(String[] args) {
        if(args.length == 0) return false;
        return isValidReadCommand(args) ||
                isValidWriteCommand(args) ||
                isValidEraseCommand(args) ||
                isValidFlushCommand(args);
    }

    private void write(int lba, String value) {
        if (inputHandler.isFull()) {
            flushBuffer();
        }

        inputHandler.add(String.join(" ", "W", String.valueOf(lba), value));

        clear();
    }

    private void read(int lba) {
        String ret = inputHandler.read(lba);

        if (ret.isBlank()) {
            ret = ssd.read(lba);
        }

        outputHandler.write(SSD_OUTPUT_TXT, ret.getBytes());
    }

    private void erase(int lba, int size) {
        if (inputHandler.isFull()) {
            flushBuffer();
        }

        inputHandler.add(String.join(" ", "E", String.valueOf(lba), String.valueOf(size)));

        clear();
    }

    private void flushBuffer() {
        List<String> commands = inputHandler.flush();

        for (String cmd : commands) {
            String[] parts = cmd.split(" ");
            String mode = parts[0];
            int lba = Integer.parseInt(parts[1]);
            String value = parts[2];

            if (mode.equals("W")) {
                ssd.write(lba, value);
            } else if (mode.equals("E")) {
                ssd.erase(lba, Integer.parseInt(value));
            }
        }
        clear();
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();

            String mode = args[0];
            int lba = args.length >= 2 ? Integer.parseInt(args[1]) : -1;
            String value = args.length >= 3 ? args[2] : "";
            switch (mode) {
                case "R" -> {
                    read(lba);
                }
                case "W" -> {
                    write(lba, value);
                }
                case "E" -> {
                    erase(lba, Integer.parseInt(value));
                }
                case "F" -> {
                    this.flushBuffer();
                }
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            error();
        }
    }

    public static final class SsdManagerBuilder {
        private ReadWritable ssd;
        private InputHandler inputHandler;
        private OutputHandler outputHandler;

        private SsdManagerBuilder() {
        }

        public static SsdManagerBuilder builder() {
            return new SsdManagerBuilder();
        }

        public SsdManagerBuilder withSsd(ReadWritable ssd) {
            this.ssd = ssd;
            return this;
        }

        public SsdManagerBuilder withInputHandler(InputHandler inputHandler) {
            this.inputHandler = inputHandler;
            return this;
        }

        public SsdManagerBuilder withOutputHandler(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
            return this;
        }

        public SsdManager build() {
            return new SsdManager(this);
        }
    }

    public static SsdManagerBuilder builder() {
        return new SsdManagerBuilder();
    }
}
