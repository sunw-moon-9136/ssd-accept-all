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
                handleWrite(parseInt(parts[1]), Long.decode(parts[2]));
                break;
            case "E":
                handleErase(parseInt(parts[1]), parseInt(parts[2]));
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

    private void handleWrite(int address, long value) {
        buffer.removeIf(c -> c.type == Command.Type.WRITE && c.address == address);
        buffer.addLast(Command.write(address, value));
        List<Command> updates = splitOverlappingErases(address);
        buffer.addAll(updates);
    }

    private void ignoreCommand() {
//
//        Iterator<Command> it = buffer.iterator();
//        while (it.hasNext()) {
//            Command c = it.next();
//            if (c.type == Command.Type.WRITE) {
//
//
//            }
        //                it.remove();
//            } else if (c.type == Command.Type.ERASE) {
//                int s = c.address, e = c.address + c.size - 1;
//                if ((s <= start && end <= e)) {
//                    shouldAdd = false;
//                }
//                if (s >= start && e <= end) {
//                    it.remove();
//                }
//            }
//        }

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

    private void handleErase(int address, int size) {
        if (size <= 0) return;
        int start = address, end = address + size - 1;

        boolean shouldAdd = true;
        Iterator<Command> it = buffer.iterator();
        while (it.hasNext()) {
            Command c = it.next();
            if (c.type == Command.Type.WRITE && c.address >= start && c.address <= end) {
                it.remove();
            } else if (c.type == Command.Type.ERASE) {
                int s = c.address, e = c.address + c.size - 1;
                if ((s <= start && end <= e)) {
                    shouldAdd = false;
                }
                if (s >= start && e <= end) {
                    it.remove();
                }
            }
        }
        if (shouldAdd) buffer.addLast(Command.erase(address, size));
        mergeErases();
    }

    private void mergeErases() {
        List<Command> erases = buffer.stream().filter(c -> c.type == Command.Type.ERASE).sorted(Comparator.comparingInt(c -> c.address)).collect(Collectors.toList());
        if (erases.size() < 2) return;

        List<Command> merged = new ArrayList<>();
        Command prev = erases.get(0);
        merged.add(prev);

        for (int i = 1; i < erases.size(); i++) {
            Command curr = erases.get(i);
            if (prev.address + prev.size >= curr.address) {
                int combined = prev.size + curr.size - Math.max(0, prev.address + prev.size - curr.address);
                if (combined <= 10) {
                    prev.size = combined;
                } else {
                    prev.size = 10;
                    merged.add(Command.erase(prev.address + 10, combined - 10));
                }
            } else {
                merged.add(curr);
                prev = curr;
            }
        }
        buffer.removeIf(c -> c.type == Command.Type.ERASE);
        buffer.addAll(merged);
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
