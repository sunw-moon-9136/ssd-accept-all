import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Buffer {
    class Command {
        int order = -1;
        String mode = "";
        int lba = -1;
        String value = "";

        public Command() {
        }

        public Command(int order, String mode, int lba, String value) {
            this.order = order;
            this.mode = mode;
            this.lba = lba;
            this.value = value;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public String toString() {
            return String.join("_", String.valueOf(order), mode, String.valueOf(lba), value);
        }

    }


    public static final String DEFAULT_ZERO_VALUE = "0x00000000";
    private static final int MAX_BUFFER_SIZE = 5;
    private static final int RAW_SIZE = 100;

    File folder = new File("buffer");
    private final List<Command> buffer = new ArrayList<>();
    private final int[] raw = new int[100];

    public Buffer() {
        this.init();
    }

    public void addCommand(Command command) {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(String.valueOf(command.order) + "_");
                }
            });
            if (files != null && files.length == 1 && files[0].getName().equals(String.valueOf(command.order) + "_empty.txt")) {
                File newFile = new File("buffer/" + command.toString() + ".txt");
                boolean succcess = files[0].renameTo(newFile);
                if (succcess) {
                    buffer.add(command);
                }
            }
        }
    }

    public void erase(int lba,int size){
        if(buffer.size() < MAX_BUFFER_SIZE) {
            Command command = new Command(buffer.size() + 1, "E", lba, String.valueOf(size));
            addCommand(command);
        }
    }

    public void write(int lba, String value) {
        if (buffer.size() < MAX_BUFFER_SIZE) {
            Command command = new Command(buffer.size() + 1, "W", lba, value);
            addCommand(command);
        }
    }

    public String read(int lba) {
        init();

        int value = raw[lba];
        String ret = null;
        if (value >= 0) {
            ret = String.format("0x%08X", value);
        } else if (value == -1) {
            ret = DEFAULT_ZERO_VALUE;
        }

        return ret;
    }

    public List<Command> flush() {
        // buffer 하위 파일들을 모두 제거한다
        deleteBufferFiles();

        // TODO: 버퍼에 있는 명령어를 모두 SSD에 적용한다
        return buffer;
    }

    private void deleteBufferFiles() {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    boolean isFull() {
        return buffer.size() == MAX_BUFFER_SIZE;
    }

    public void init() {
        createFolderAndFilesIfNotExist();
        initBufferWithFiles();
        initRawData();
    }

    private void initRawData() {
        for (int i = 0; i < RAW_SIZE; i++) {
            raw[i] = -2;
        }

        for (int i = 0; i < buffer.size(); i++) {
            Command c = buffer.get(i);

            int index = c.lba;

            // W는 실제 값을 10진수로 기록
            if (c.mode.equals("W")) {
                int value = Integer.decode(c.value);
                raw[index] = value;
                System.out.println(value);
            }
            // E는 -1로 기록
            else if (c.mode.equals("E")) {
                int size = Integer.parseInt(c.value);
                for (int j = index; j <= index + size - 1; j++) {
                    raw[j] = -1;
                }
            }
        }
    }

    private void initBufferWithFiles() {
        buffer.clear();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    Command command = parseFileNameToCommand(fileName);

                    if (command != null) {
                        buffer.add(command);
                    }
                }
            }
            buffer.sort(Comparator.comparingInt(Command::getOrder));
        }
    }

    private Command parseFileNameToCommand(String fileName) {
        String name = fileName.substring(0, fileName.lastIndexOf('.'));
        String[] parts = name.split("_");

        // index_empty는 length가 2
        // W나 E의 길이는 4
        if (parts.length == 4) {
            try {
                int order = Integer.parseInt(parts[0]);
                String commandType = parts[1];
                int lba = Integer.parseInt(parts[2]);
                String value = parts[3];
                return new Command(order, commandType, lba, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void createFolderAndFilesIfNotExist() {
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (int i = 1; i <= 5; i++) {
            // index_ 로 시작하는 파일을 탐색
            int index = i;
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(String.valueOf(index) + "_");
                }
            });

            // 파일이 없으면 empty 파일 생성
            if (files.length != 1) {
                File file = new File(folder, i + "_empty.txt");

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
