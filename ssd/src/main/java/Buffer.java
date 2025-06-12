import javax.annotation.processing.FilerException;
import java.io.File;
import java.io.FileNotFoundException;
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

    File folder = new File("buffer");
    private List<Command> buffer = new ArrayList<>();

    public Buffer() {
        this.init();
    }

    private void deleteMarkedCommand() {
        // 삭제되어야 할 명령을 뺀다
        List<Command> tmp = new ArrayList<>();
        for (Command command : buffer) {
            System.out.println(command);
        }
        for (Command command : buffer) {
            if (command.order != -1) {
                command.order = tmp.size() + 1;
                tmp.add(command);
            }
        }
        buffer = tmp;
        for (Command command : buffer) {
            System.out.println(command);
        }
    }

    private void ignoreWriteCommand() {
        Command command = buffer.get(buffer.size() - 1);
        if (!command.mode.equals("W")) return;
        for (int i = buffer.size() - 2; i >= 0; i--) {
            Command prev = buffer.get(i);
            // W W :에 같은 주소면 최신 W 만 남겨둔다.
            if (prev.mode.equals("W") && prev.lba == command.lba) {
                System.out.printf("Ignoring command: %d, %d\n", prev.order, prev.lba);
                prev.order = -1;
            }
            // W E :의 경우 E가 단 한 곳만 지우는 경우 빼는 의미가 있다 (개수가 줄어듬)
            // 가령 W 를 E 2개로 나눠버리면 명령 개수가 늘어난다.
            // 만약 범위 양 끝쪽에서 짜르면 명령 개수는 그대로다
            // ignoreCommand의 목적은 명령을 줄이는 것이므로, 개수가 그대로일때도 패스한다.
            if (prev.mode.equals("E") && prev.lba == command.lba && Integer.parseInt(prev.value) == 1) {
                System.out.printf("Ignoring command: %d, %d\n", prev.order, prev.lba);
                prev.order = -1;
            }
        }
        deleteMarkedCommand();
    }

    boolean isWriteCommandInEraseCommand(int wLBA, int eLBA, int size) {
        int maxLBA = eLBA + size - 1;
        return wLBA >= eLBA && wLBA <= maxLBA;
    }

    boolean isEraseCommandInEraseCommand(Command prev, Command next) {
        int pL = prev.lba;
        int pR = prev.lba + Integer.parseInt(prev.value) - 1;
        int nL = next.lba;
        int nR = next.lba + Integer.parseInt(next.value) - 1;

        if ((pR >= nL && pR <= nR) || (nR >= pL && nR <= pR)) {
            return true;
        }
        return false;
    }

    private List<Command> mergeErase(Command prev, Command next) {
        List<Command> commands = new ArrayList<>();
        int maxPLBA = prev.lba + Integer.parseInt(prev.value) - 1;
        int maxNRBA = next.lba + Integer.parseInt(next.value) - 1;
        int minX = Math.min(prev.lba, next.lba);
        int maxX = Math.max(maxPLBA, maxNRBA);
        while (minX <= maxX) {
            Command newCommand = new Command(buffer.size() + 1, "E", minX, String.valueOf(Math.min(maxX - minX + 1, 10)));
            commands.add(newCommand);
            minX += 10;
        }
        return commands;
    }

    private void ignoreEraseCommand() {
        Command command = buffer.get(buffer.size() - 1);
        if (!command.mode.equals("E")) return;
        for (int i = buffer.size() - 2; i >= 0; i--) {
            Command prev = buffer.get(i);
            // E W :W가 E 범위에 속하면 삭제한다.
            if (prev.mode.equals("W") && isWriteCommandInEraseCommand(prev.lba, command.lba, Integer.parseInt(command.value))) {
                prev.order = -1;
            }
            // E E : E와 E의 범위가 서로 겹치면 합친다(10을 넘으면 나눈다)
            if (i == buffer.size() - 2 && prev.mode.equals("E") && isEraseCommandInEraseCommand(prev, command)) {
                List<Command> newCommand = mergeErase(prev, command);
                prev.order = -1;
                command.order = -1;
                buffer.addAll(newCommand);
                break;
            }
        }
        deleteMarkedCommand();
    }

    private void ignoreCommand() {
        ignoreWriteCommand();
        ignoreEraseCommand();
    }

    public void addCommand(Command command) {
        buffer.add(command);
        ignoreCommand();
        initFilesWithBuffer();
    }

    private void initFilesWithBuffer() {
        deleteBufferFiles();
        initEmptyTextFile();
        for (Command command : buffer) {
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(command.order + "_");
                    }
                });
                if (files != null && files.length == 1) {
                    File newFile = new File("buffer/" + command + ".txt");
                    boolean success = files[0].renameTo(newFile);
                    if (!success) throw new RuntimeException();
                }
            }
        }
    }

    public void erase(int lba, int size) {
        if (buffer.size() < MAX_BUFFER_SIZE) {
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

    private String fastRead(int lba) {
        String ret = null;
        // 가장 최신순부터 확인하면서, 해당 주소에 적용된 명령 값을 확인한다.
        // E -> 지워짐 -> 0x00000000
        // W -> 덮어쓰기 -> 해당 값
        // 없으면 buffer miss -> ssd_nand.txt
        for (int i = buffer.size() - 1; i >= 0; i--) {
            Command command = buffer.get(i);
            if (command.lba == lba) {
                if (command.mode.equals("E")) {
                    ret = "0x00000000";
                } else if (command.mode.equals("W")) {
                    ret = command.value;
                }
                break;
            }
        }
        return ret;
    }

    // Fast-Read Algorithm
    public String read(int lba) {
        init();

        return fastRead(lba);
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

    private void initEmptyTextFile() {
        for (int i = 1; i <= 5; i++) {
            // index_ 로 시작하는 파일을 탐색
            int index = i;
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(index + "_");
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

    private void createFolderAndFilesIfNotExist() {
        if (!folder.exists()) {
            folder.mkdir();
        }

        initEmptyTextFile();
    }

}
