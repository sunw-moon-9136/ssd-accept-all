package SSD;

import org.w3c.dom.ranges.Range;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Command {
    enum Type {WRITE, ERASE}

    Type type;
    int address;
    int size;
    long value;

    private Command(Type type, int address, int size, long value) {
        this.type = type;
        this.address = address;
        this.size = size;
        this.value = value;
    }

    static Command write(int address, long value) {
        return new Command(Type.WRITE, address, 0, value);
    }

    static Command erase(int address, int size) {
        return new Command(Type.ERASE, address, size, 0L);
    }
}

public class InputFileHandler implements InputHandler {
    private static final int MAX_BUFFER_SIZE = 5;
    private static final Path BUFFER_DIR = Paths.get("buffer");
    private final List<Command> buffer = new LinkedList<>();


    public InputFileHandler() {
        initDirectory();
        loadExistingCommands();
        clearAndGenerateEmpty();
    }


    @Override
    public void add(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "W":
                processWriteInsertOptimize(parseInt(parts[1]), Long.decode(parts[2]));
                break;
            case "E":
                processEraseInsertOptimize(parseInt(parts[1]), parseInt(parts[2]));
                break;
            default:
                break;
        }
        clearAndGenerateEmpty();
    }

    @Override
    public String read(int address) {
        ListIterator<Command> iterator = buffer.listIterator(buffer.size());
        while (iterator.hasPrevious()) {
            Command command = iterator.previous();
            if (command.type == Command.Type.WRITE && command.address == address) {
                return formatHex(command.value);
            }
            if (command.type == Command.Type.ERASE && command.address <= address && address <= command.address + command.size - 1) {
                return formatHex(0L);
            }
        }
        return "";
    }

    @Override
    public boolean isFull() {
        return buffer.size() == MAX_BUFFER_SIZE;
    }

    @Override
    public List<String> flush() {
        List<String> commands = buffer.stream().map(c -> c.type == Command.Type.WRITE ? String.format("W %d %s", c.address, formatHex(c.value)) : String.format("E %d %d", c.address, c.size)).collect(Collectors.toList());
        buffer.clear();
        clearAndGenerateEmpty();
        return commands;
    }

    private void processWriteInsertOptimize(int address, long value) {
        // 1.기존과 동일 주소에 Write 하는 명령이 있으면 삭제한다.
        // 순서 무관
        buffer.removeIf(c -> c.type == Command.Type.WRITE && c.address == address);

        // 2. Erase의 양쪽 끝단에 W 가 속하면 범위를 재설정
        // 순서 무관
        Iterator<Command> iterator = buffer.iterator();
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (command.type != Command.Type.ERASE || address < command.address || address > command.address + command.size - 1) {
                continue;
            }

            int start = command.address;
            int end = command.address + command.size - 1;

            // 시작 점에 속할 경우
            if (start == address) start += 1;

            // 끝점에 속할 경우
            if (end == address) end -= 1;

            // command.size() == 1 일 경우, 겹친다면 해당 명령을 삭제
            if (start > end) iterator.remove();
            else {
                // command.size() > 1 일 경우, command를 업데이트 해준다
                command.address = start;
                command.size = end - start + 1;
            }
        }

        // 명령어를 삽입
        buffer.add(Command.write(address, value));

        // 3. 현재 명령과 다른 W 명령들을 합쳤을 때, E의 범위와 동일하면 해당 E를 삭제한다
        iterator = buffer.iterator();
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (command.type != Command.Type.ERASE) continue;

            int currentIndex = buffer.indexOf(command);

            // 현재 Erase 명령 뒤에 나오는 Write 명령이 Erase 명령 범위에 포함이 되면,
            // Erase 앞에 있는 W 명령이 해당 Erase 명령을 지울수는 없다 -> 해당 범위에 있다면 이미 지워졌을 것이기 때문이다.
            // 집합에 추가한다.
            Set<Integer> writeSets = new HashSet<>();
            for (int i = currentIndex + 1; i < buffer.size(); i++) {
                Command next = buffer.get(i);
                if (next.type != Command.Type.WRITE) continue;
                if (command.address <= next.address && next.address <= command.address + command.size - 1) {
                    writeSets.add(next.address);
                }
            }

            // Write 집합이 Erase 명령을 모두 포함하면 현재 Erase 명령은 지워도 된다.
            boolean fullyCovered = true;
            for (int addr = command.address; addr <= command.address + command.size - 1; addr++) {
                if (!writeSets.contains(addr)) {
                    fullyCovered = false;
                    break;
                }
            }

            if (fullyCovered) iterator.remove();
        }
    }

    private void processEraseInsertOptimize(int address, int size) {
        if (size <= 0) return;

        int start = address;
        int end = address + size - 1;

        /*
         * 1. Write 명령이 현재 Erase 명령 범위 안에 속하면 해당 Write 명령을 지운다
         * - 순서 무관
         */
        buffer.removeIf(c -> {
            if (c.type != Command.Type.WRITE) return false;
            return start <= c.address && c.address <= end;
        });


        /*
         * 2. Erase 명령이 현재 Erase 명령 범위 안에 속하면 해당 Erase 명령을 지운다.
         * - 순서 무관
         * E 4 2
         * E 0 10 시
         * E 4 2 를 지운다.
         * 참고) 완전 동일한 범위에 대해서도 삭제가 된다.
         */
        buffer.removeIf(c -> {
            if (c.type != Command.Type.ERASE) return false;
            int prevStart = c.address;
            int prevEnd = c.address + c.size - 1;
            return start <= prevStart && prevEnd <= end;
        });

        /*
         * 만약에 현재 Erase가 이전 Erase 범위 안에 속하면
         * 현재 Erase를 넣을 필요가 없다.
         */
        boolean shouldAdd = true;
        Iterator<Command> iterator = buffer.iterator();
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (command.type != Command.Type.ERASE) continue;
            int prevSize = command.address;
            int prevEnd = command.address + command.size - 1;

            if (prevSize <= start && end <= prevEnd) {
                shouldAdd = false;
                break;
            }
        }

        boolean[] list = new boolean[100];
        for (int i = 0; i < 100; i++) list[i] = false;
        for (Command command : buffer) {
            if (command.type == Command.Type.ERASE) {
                for (int j = command.address; j <= command.address + command.size - 1; j++) list[j] = true;
            }
        }

        int startErase = -1;
        int endErase = -1;
        for(int i = address; i <= address + size - 1; i++) {
            if(!list[i]) {
                startErase = i;
                break;
            }
        }
        for(int i = address + size - 1; i >= address; i--){
            if(!list[i]) {
                endErase = i;
                break;
            }
        }

        // 현재 명령을 완전히 커버하는 경우
        if(startErase == -1 || endErase == -1) return;

        if (shouldAdd) {
            mergeEraseCommand(address, size);
        }
    }

    private void mergeEraseCommand(int address, int size) {
        // 연속된 E들을 모아서 합친 다음에 다시 넣는다.
        // 연속된 E들을 다시 넣을 때는 순서를 정렬해서 넣어도 작동에는 문제가 없다.
        buffer.add(Command.erase(address, size));

        List<Command> eraseCommands = new LinkedList<>();

        // 먼저 buffer에서 뒤에서부터 연속으로 나오는 Erase를 모아준다.
        while (!buffer.isEmpty() && buffer.get(buffer.size() - 1).type == Command.Type.ERASE) {
            eraseCommands.add(buffer.remove(buffer.size() - 1));
        }

        // 0~99 의 범위에서 Erase 에 적용되는 범위를 체크한다.
        LinkedList<Command> newEraseCommands = new LinkedList<>();
        boolean[] list = new boolean[100];
        for (int i = 0; i < 100; i++) list[i] = false;
        for (Command cmd : eraseCommands) {
            int start = cmd.address;
            int end = cmd.address + cmd.size - 1;
            for (int i = start; i <= end; i++) list[i] = true;
        }

        // Erase가 적용되는 범위들을 재조립
        int count = 0;
        for (int i = 0; i < 100; i++) {
            if (list[i]) {
                count += 1;

                // address의 마지막 부분에서 예외 처리
                if (count == 10) {
                    newEraseCommands.add(Command.erase(i - count + 1, 10));
                    count = 0;
                }
                 else if(i == 99){
                    newEraseCommands.add(Command.erase(99 - count + 1, count));
                }
            } else if (count > 0) {
                newEraseCommands.add(Command.erase(i - count, count));
                count = 0;
            }
        }
        buffer.addAll(newEraseCommands);
    }

    private void clearAndGenerateEmpty() {
        try {
            Files.createDirectories(BUFFER_DIR);
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(BUFFER_DIR, "*.txt")) {
                for (Path f : ds) Files.deleteIfExists(f);
            }
            for (int i = 0; i < MAX_BUFFER_SIZE; i++) {
                String name = (i < buffer.size()) ? slotFileName(i + 1, buffer.stream().skip(i).findFirst().orElse(null)) : String.format("%d_empty.txt", i + 1);
                Files.writeString(BUFFER_DIR.resolve(name), "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String slotFileName(int slot, Command c) {
        if (c == null) return String.format("%d_empty.txt", slot);
        if (c.type == SSD.Command.Type.WRITE) {
            return String.format("%d_W_%d_%s.txt", slot, c.address, formatHex(c.value));
        } else {
            return String.format("%d_E_%d_%d.txt", slot, c.address, c.size);
        }
    }

    private void initDirectory() {
        try {
            Files.createDirectories(BUFFER_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 이름 순서대로 가져오기
    private void loadExistingCommands() {
        try (Stream<Path> files = Files.list(BUFFER_DIR)) {
            List<String> cmds = files
                    .filter(f -> {
                        String name = f.getFileName().toString();
                        return name.endsWith(".txt") && (name.contains("_W_") || name.contains("_E_"));
                    })
                    // 숫자 접두사로 정렬
                    .sorted(Comparator.comparingInt(f -> {
                        String name = f.getFileName().toString();
                        try {
                            return Integer.parseInt(name.split("_")[0]);
                        } catch (NumberFormatException e) {
                            return Integer.MAX_VALUE; // 숫자 파싱 실패 시 맨 뒤로
                        }
                    }))
                    .map(f -> f.getFileName().toString())
                    .map(n -> n.substring(n.indexOf('_') + 1) // 앞 숫자 제거
                            .replace(".txt", "")
                            .replace('_', ' '))
                    .collect(Collectors.toList());

            buffer.clear();
            cmds.forEach(this::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int parseInt(String s) {
        return Integer.parseInt(s);
    }

    private static String formatHex(long v) {
        return String.format("0x%08X", v);
    }
}
