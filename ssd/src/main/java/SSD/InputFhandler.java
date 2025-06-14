package SSD;

import org.w3c.dom.ranges.Range;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputFhandler implements InputHandler {
    private static final int MAX_BUFFER_SIZE = 5;
    private static final Path BUFFER_DIR = Paths.get("buffer");
    private final List<Command> buffer = new LinkedList<>();


    public InputFhandler() {
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
            if(c.type != Command.Type.ERASE) return false;
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

            if(prevSize <= start && end <= prevEnd) {
                shouldAdd = false;
                break;
            }
        }

        if(shouldAdd) {
            buffer.add(Command.erase(address, size));
        }

        mergeEraseCommand();
    }

    private void mergeEraseCommand() {

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

    private void loadExistingCommands() {
        try (Stream<Path> files = Files.list(BUFFER_DIR)) {
            List<String> cmds = files.map(Path::getFileName).map(Path::toString).filter(n -> (n.endsWith(".txt") && (n.contains("_W_") || n.contains("_E_")))).map(n -> n.substring(n.indexOf('_') + 1).replace(".txt", "").replace('_', ' ')).collect(Collectors.toList());
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
