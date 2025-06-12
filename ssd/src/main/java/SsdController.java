import java.util.ArrayList;
import java.util.List;

public class SsdController {
    private Driver driver;
    private ReadWritable disk;
    private Buffer buffer;

    public static final String SSD_OUTPUT_TXT = "ssd_output.txt";
    public static final byte[] ERROR_BYTES = "ERROR".getBytes();
    public static final byte[] EMPTY_BYTES = "".getBytes();

    public SsdController() {
        this.driver = new FileDriver();
        this.disk = new Ssd();
        this.buffer = new Buffer();
    }

    public SsdController(Driver driver, ReadWritable disk) {
        this.driver = driver;
        this.disk = disk;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setDisk(Ssd disk) {
        this.disk = disk;
    }

    public void error() {
        driver.write(SSD_OUTPUT_TXT, ERROR_BYTES);
    }

    public void clear() {
        driver.write(SSD_OUTPUT_TXT, EMPTY_BYTES);
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
        int maxLBA = Integer.parseInt(args[1]) + (args[2].equals("0") ? 0 : Integer.parseInt(args[2]) - 1);
        return args.length == 3 &&
                args[0].equals("E") &&
                isValidLBA(args[1]) &&
                isValidEraseSize(Integer.parseInt(args[2])) &&
                isValidLBA(String.valueOf(maxLBA));
    }

    private boolean isValidArgs(String[] args) {
        return isValidReadCommand(args) ||
                isValidWriteCommand(args) ||
                isValidEraseCommand(args);
    }

    private void write(int lba, String value) {
        // TODO: BUFFER에 쓰기, SIZE check랑 full이면 flush
        if (buffer.isFull()) {
            handleBufferFlush();
        }
        buffer.write(lba,value);
        clear();
    }

    private void read(int lba) {
        // TODO: BUFFER에서 가져오기
        String ret = buffer.read(lba);

        // TODO: 없으면 아래 코드
        if(ret == null) {
            ret = disk.read(lba);
        }

        driver.write(SSD_OUTPUT_TXT, ret.getBytes());
    }

    private void erase(int lba, int size) {
        // TODO: BUFFER에 쓰기, SIZE check랑 full이면 flush
        if(size == 0) return;
        if (buffer.isFull()) {
            handleBufferFlush();
        }
        buffer.erase(lba,size);
        clear();
    }

    private void handleBufferFlush() {
        List<Buffer.Command> commands = buffer.flush();
        for (Buffer.Command c : commands) {
            if (c.mode.equals("W")) {
                disk.write(c.lba,c.value);
            } else if(c.mode.equals("E")) {
                disk.erase(c.lba, Integer.parseInt(c.value));
            }
        }
        buffer.init();
    }

    public void run(String[] args) {
        try {
            if (!isValidArgs(args)) throw new IllegalArgumentException();

            // cmd: ssd mode lba [value]
            String mode = args[0];
            int lba = Integer.parseInt(args[1]);
            String value = args.length >= 3 ? args[2] : "";
            switch (mode) {
                case "R" ->{
                    read(lba);
                }
                case "W"->{
                    write(lba, value);
                }
                case "E"->{
                    erase(lba, Integer.parseInt(value));
                }
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            error();
        }
    }
}
