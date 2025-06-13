package SSD;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Command {
    enum Type {WRITE, ERASE}

    final Type type;
    final int address;
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
    private final Deque<Command> buffer = new ArrayDeque<>();

    public InputFileHandler() {
        initDirectory();
        loadExistingCommands();
        clearAndGenerateEmpty();
    }

    @Override
    public void add(String input) {
        String[] parts = input.split("\\s+");
        switch (parts[0].toUpperCase()) {
            case "W":
                processWriteInsertOptimize(parseInt(parts[1]), Long.decode(parts[2]));
                break;
            case "E":
                processEraseInsertOptimize(parseInt(parts[1]), parseInt(parts[2]));
                break;
            default:
                return;
        }
        trimBufferSize();
        clearAndGenerateEmpty();
    }

    @Override
    public String read(int address) {
        for (Iterator<Command> it = buffer.descendingIterator(); it.hasNext(); ) {
            Command c = it.next();
            if (c.type == Command.Type.WRITE && c.address == address) {
                return formatHex(c.value);
            }
        }
        String eraseValue = buffer.stream().filter(c -> c.type == Command.Type.ERASE).filter(c -> address >= c.address && address < c.address + c.size).map(c -> formatHex(0L)).findFirst().orElse("");
        return eraseValue;
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
        buffer.removeIf(c -> c.type == Command.Type.WRITE && c.address == address);
        buffer.addLast(Command.write(address, value));
        ignoreEraseWithCombinedWrites(address);
        List<Command> updates = splitOverlappingErases(address);
        for (int i = 0; i < updates.size(); i++) buffer.addFirst(updates.get(i));
//        buffer.addAll(updates);
    }

    private void ignoreEraseWithCombinedWrites(int address) {
        Iterator<Command> iterator = buffer.iterator();

        while (iterator.hasNext()) {
            Command command = iterator.next();

            if (command.type != Command.Type.ERASE) continue;

            int start = command.address;
            int end = command.address + command.size - 1;

            if (address < start || address > end) continue;

            Set<Integer> eraseRange = getEraseRangeSet(start, end);
            Set<Integer> writeAddresses = getWriteAddressesInEraseRange(eraseRange);

            if (eraseRange.equals(writeAddresses)) {
                iterator.remove();
            }
        }
    }

    private Set<Integer> getEraseRangeSet(int start, int end) {
        Set<Integer> range = new HashSet<>();
        for (int i = start; i <= end; i++) {
            range.add(i);
        }
        return range;
    }

    private Set<Integer> getWriteAddressesInEraseRange(Set<Integer> range) {
        Set<Integer> writeSet = new HashSet<>();
        for (Command cmd : buffer) {
            if (cmd.type == Command.Type.WRITE && range.contains(cmd.address)) {
                writeSet.add(cmd.address);
            }
        }
        return writeSet;
    }


    private List<Command> splitOverlappingErases(int address) {
        List<Command> updates = new ArrayList<>();
        for (Command c : buffer.stream().filter(c -> c.type == Command.Type.ERASE).collect(Collectors.toList())) {
            if (c.address == address) {
                buffer.remove(c);
                if (c.size > 1) updates.add(Command.erase(address + 1, c.size - 1));
            } else if (c.address + c.size - 1 == address) {
                buffer.remove(c);
                if (c.size > 1) updates.add(Command.erase(c.address, c.size - 1));
            }
        }
        return updates;
    }

    private void processEraseInsertOptimize(int address, int size) {
        if (size <= 0) return;

        int inputCmdStart = address;
        int inputCmdEnd = address + size - 1;
        boolean shouldAdd = true;

        Iterator<Command> it = buffer.iterator();
        while (it.hasNext()) {
            Command c = it.next();
            if (c.type == Command.Type.WRITE && c.address >= inputCmdStart && c.address <= inputCmdEnd) {
                it.remove();
            } else if (c.type == Command.Type.ERASE) {
                int cStart = c.address;
                int cEnd = c.address + c.size - 1;
                if ((cStart <= inputCmdStart && inputCmdEnd <= cEnd)) shouldAdd = false;
                if (cStart >= inputCmdStart && cEnd <= inputCmdEnd) it.remove();
            }
        }
        if (shouldAdd) buffer.addLast(Command.erase(address, size));
        mergeEraseCommand();
    }

    private void mergeEraseCommand() {
        if (buffer.size() < 2) return;

        List<Command> tempBuffer = new ArrayList<>(buffer);
        List<Command> mergedBuffer = new ArrayList<>();

        int index = 0;
        while (index < tempBuffer.size()) {
            Command current = tempBuffer.get(index);
            if (current.type == Command.Type.ERASE) {
                List<Command> eraseGroup = collectConsecutiveErases(tempBuffer, index);
                if (eraseGroup.size() >= 2) {
                    mergedBuffer.addAll(combineOverlappingErases(eraseGroup));
                } else {
                    mergedBuffer.add(current);
                }
                index += eraseGroup.size();
            } else {
                mergedBuffer.add(current);
                index++;
            }
        }
        buffer.clear();
        buffer.addAll(mergedBuffer);
    }

    // 주어진 인덱스부터 연속된 Erase 명령어를 수집하는 헬퍼 메서드
    private List<Command> collectConsecutiveErases(List<Command> commands, int startIndex) {
        List<Command> eraseGroup = new ArrayList<>();
        for (int i = startIndex; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            if (cmd.type == Command.Type.ERASE) {
                eraseGroup.add(cmd);
            } else {
                break;
            }
        }
        return eraseGroup;
    }

    // 연속된 Erase 명령어를 Merge
    private List<Command> combineOverlappingErases(List<Command> eraseCommands) {
        List<Command> mergedErases = new ArrayList<>();
        Command baseCommand = eraseCommands.get(0);
        mergedErases.add(baseCommand);

        for (int i = 1; i < eraseCommands.size(); i++) {
            Command currentCommand = eraseCommands.get(i);
            // 이전 명령어와 주소가 겹치거나 인접한 경우 Merge
            if (baseCommand.address + baseCommand.size >= currentCommand.address) {
                int overlap = Math.max(0, baseCommand.address + baseCommand.size - currentCommand.address);
                int totalSize = baseCommand.size + currentCommand.size - overlap;

                if (totalSize <= 10) {
                    baseCommand.size = totalSize;
                } else {
                    baseCommand.size = 10;
                    mergedErases.add(Command.erase(baseCommand.address + 10, totalSize - 10));
                }
            } else {
                mergedErases.add(currentCommand);
                baseCommand = currentCommand;
            }
        }
        return mergedErases;
    }

    private void trimBufferSize() {
        while (buffer.size() > MAX_BUFFER_SIZE) {
            buffer.removeFirst();
        }
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
