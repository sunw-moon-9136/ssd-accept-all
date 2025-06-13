import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SsdCommandBufferOptimizer {
    private static final int MAX_BUFFER_SIZE = 5;
    private final List<Command> buffer = new ArrayList<>();
    private final String BUFFER_PATH = "buffer";

    private static class Command {
        enum Type {WRITE, ERASE}

        Type type;
        int address;
        int size;
        long value;

        Command(int address, long value) {
            this.type = Type.WRITE;
            this.address = address;
            this.value = value;
        }

        Command(int address, int size, boolean isErase) {
            this.type = Type.ERASE;
            this.size = size;
            this.address = address;
        }
    }


    public SsdCommandBufferOptimizer() {
        initializeFromFiles();
    }

    private void removeAllFileBufferFolder() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BUFFER_PATH));
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    private void initializeFromFiles() {
        try {
            List<Path> fileList = Files.list(Paths.get(BUFFER_PATH)).filter(p -> p.toString().endsWith(".txt")).collect(Collectors.toList());
            removeAllFileBufferFolder();
            for (Path path : fileList) {
                String cmd = path.getFileName().toString().substring(2).split(".txt")[0];
                if (!cmd.isEmpty() && (cmd.startsWith("W") || cmd.startsWith("E"))) {
                    add(cmd.replace("_", " "));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFileStartWith(String string) {
        try (Stream<Path> files = Files.list(Path.of(BUFFER_PATH))) {
            files.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().startsWith(string)).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // add(String command): WRITE/ERASE 명령 추가 및 최적화 수행
    public void add(String command) {
        String[] parts = command.split("\\s+");
        String op = parts[0].toUpperCase();
        switch (op) {
            case "W": {
                int lba = Integer.parseInt(parts[1]);
                long value = Long.decode(parts[2]);

                buffer.removeIf(c -> c.type == Command.Type.WRITE && c.address == lba);
                buffer.add(new Command(lba, value));


                //WRITE가 기존에 존재하는 ERASE 앞뒤인 경우(특이케이스)
                // ERASE (address == lba)
                Optional<Command> eraseCmdOpt = buffer.stream()
                        .filter(c -> c.type == Command.Type.ERASE && c.address == lba)
                        .findFirst();
                eraseCmdOpt.ifPresent(cmd -> {
                    buffer.remove(cmd);
                    if (cmd.size - 1 > 0) buffer.add(new Command(lba + 1, cmd.size - 1, true));
                });

                // ERASE (address + size - 1 == lba)
                Optional<Command> eraseCmdOpt2 = buffer.stream()
                        .filter(c -> c.type == Command.Type.ERASE && c.address + c.size - 1 == lba)
                        .findFirst();
                eraseCmdOpt2.ifPresent(cmd -> {
                    buffer.remove(cmd);
                    if (cmd.size - 1 > 0) buffer.add(new Command(cmd.address, cmd.size - 1, true));
                });


                break;
            }
            case "E": {
                int lba = Integer.parseInt(parts[1]);
                int size = Integer.parseInt(parts[2]);
                int eraseStart = lba, eraseEnd = lba + size - 1;

                if (size <= 0) return;

                Iterator<Command> it = buffer.iterator();
                while (it.hasNext()) {
                    Command c = it.next();
                    if (c.type == Command.Type.WRITE) {
                        if (c.address >= eraseStart && c.address <= eraseEnd) {
                            it.remove();
                        }
                    } else if (c.type == Command.Type.ERASE) {
                        int prevStart = c.address, prevEnd = c.address + c.size - 1;
                        if (eraseStart <= prevStart || prevEnd >= eraseEnd) {
                            it.remove();
                        }
                    }
                }
                buffer.add(new Command(lba, size, true));
                mergeAdjacentErases();
                break;
            }
            default:
                return;
        }

        if (buffer.size() > MAX_BUFFER_SIZE) {
            buffer.remove(0);
        }

        for (int i = 0; i < MAX_BUFFER_SIZE; i++) {
            deleteFileStartWith(String.valueOf(i + 1));
            String filename = BUFFER_PATH + "/";
            String content = "";
            if (i < buffer.size()) {
                Command c = buffer.get(i);
                if (c.type == Command.Type.WRITE) {
                    String hexVal = String.format("0x%08X", c.value);
                    filename += (i + 1) + "_W_" + c.address + "_" + hexVal + ".txt";
                } else {  // ERASE
                    filename += (i + 1) + "_E_" + c.address + "_" + c.size + ".txt";
                }
            } else {
                filename += (i + 1) + "_empty.txt";  // 빈 슬롯
            }
            // 파일 생성/갱신
            try {
                Path path = Path.of(filename);
                Files.writeString(path, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String read(int address) {
        for (int i = buffer.size() - 1; i >= 0; i--) {
            Command c = buffer.get(i);
            if (c.type == Command.Type.WRITE && c.address == address) {
                return String.format("0x%08X", c.value);
            }
        }
        for (Command c : buffer) {
            if (c.type == Command.Type.ERASE) {
                if (address >= c.address && address < c.address + c.size) {
                    return String.format("0x%08X", 0);
                }
            }
        }
        return "";
    }

    public int size() {
        return buffer.size();
    }

    public List<String> flush() {
        List<String> out = new ArrayList<>();
        for (Command c : buffer) {
            if (c.type == Command.Type.WRITE) {
                String hexVal = String.format("0x%08X", c.value);
                out.add("W " + c.address + " " + hexVal);
            } else if (c.type == Command.Type.ERASE) {
                out.add("E " + c.address + " " + c.size);
            }
        }
        removeAllFileBufferFolder();
        buffer.clear();

        //generate emptyfiles
        for (int i = 0; i < MAX_BUFFER_SIZE; i++) {
            try {
                String filename = BUFFER_PATH + "/" + (i + 1) + "_empty.txt";
                Path path = Path.of(filename);
                Files.writeString(path, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return out;
    }

    public void optimize() {
        mergeAdjacentErases();
    }

    private void mergeAdjacentErases() {
        List<Command> erases = new ArrayList<>();
        for (Command c : buffer) {
            if (c.type == Command.Type.ERASE) {
                erases.add(c);
            }
        }
        if (erases.isEmpty()) return;
        erases.sort(Comparator.comparingInt(c -> c.address));
        List<Command> merged = new ArrayList<>();
        Command prev = erases.get(0);
        merged.add(prev);
        for (int i = 1; i < erases.size(); i++) {
            Command curr = erases.get(i);
            if (prev.address + prev.size >= curr.address) {
                int combined = prev.size + curr.size - (prev.address + prev.size - curr.address);
                if (combined <= 10) {
                    prev.size = combined;
                } else {
                    // 크기 초과 시 10으로 제한하고 나머지 새 ERASE로 처리
                    int remainder = combined - 10;
                    prev.size = 10;
                    merged.add(new Command(prev.address + 10, remainder, true));
                }
            } else {
                merged.add(curr);
                prev = curr;
            }
        }
        buffer.removeIf(c -> c.type == Command.Type.ERASE);
        buffer.addAll(merged);
    }
}
