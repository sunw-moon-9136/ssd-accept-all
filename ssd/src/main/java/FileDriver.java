import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileDriver implements Driver {

    public static final String NAND_INIT_TEXT = "0 0x00000000\n1 0x00000000\n2 0x00000000\n3 0x00000000\n4 0x00000000\n5 0x00000000\n6 0x00000000\n7 0x00000000\n8 0x00000000\n9 0x00000000\n10 0x00000000\n11 0x00000000\n12 0x00000000\n13 0x00000000\n14 0x00000000\n15 0x00000000\n16 0x00000000\n17 0x00000000\n18 0x00000000\n19 0x00000000\n20 0x00000000\n21 0x00000000\n22 0x00000000\n23 0x00000000\n24 0x00000000\n25 0x00000000\n26 0x00000000\n27 0x00000000\n28 0x00000000\n29 0x00000000\n30 0x00000000\n31 0x00000000\n32 0x00000000\n33 0x00000000\n34 0x00000000\n35 0x00000000\n36 0x00000000\n37 0x00000000\n38 0x00000000\n39 0x00000000\n40 0x00000000\n41 0x00000000\n42 0x00000000\n43 0x00000000\n44 0x00000000\n45 0x00000000\n46 0x00000000\n47 0x00000000\n48 0x00000000\n49 0x00000000\n50 0x00000000\n51 0x00000000\n52 0x00000000\n53 0x00000000\n54 0x00000000\n55 0x00000000\n56 0x00000000\n57 0x00000000\n58 0x00000000\n59 0x00000000\n60 0x00000000\n61 0x00000000\n62 0x00000000\n63 0x00000000\n64 0x00000000\n65 0x00000000\n66 0x00000000\n67 0x00000000\n68 0x00000000\n69 0x00000000\n70 0x00000000\n71 0x00000000\n72 0x00000000\n73 0x00000000\n74 0x00000000\n75 0x00000000\n76 0x00000000\n77 0x00000000\n78 0x00000000\n79 0x00000000\n80 0x00000000\n81 0x00000000\n82 0x00000000\n83 0x00000000\n84 0x00000000\n85 0x00000000\n86 0x00000000\n87 0x00000000\n88 0x00000000\n89 0x00000000\n90 0x00000000\n91 0x00000000\n92 0x00000000\n93 0x00000000\n94 0x00000000\n95 0x00000000\n96 0x00000000\n97 0x00000000\n98 0x00000000\n99 0x00000000";
    public static final String NAND_FILE_NAME = "ssd_nand.txt";
    public static final String OUTPUT_FILE_NAME = "ssd_output.txt";

    @Override
    public String read(String file) {
        requireValidFileName(file);

        Path path = Paths.get(file);
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            write(file, (NAND_INIT_TEXT).getBytes());
            return read(file);
        } catch (IOException e) {
            System.out.println("Not Excpected Error");
            return "";
        }
    }

    @Override
    public void write(String file, byte[] bytes) {
        requireValidFileName(file);

        try {
            Path path = Paths.get(file);
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requireValidFileName(String file) {
        if (isNullOrEmpty(file) || !(file.equals(OUTPUT_FILE_NAME) || file.equals(NAND_FILE_NAME)))
            throw new IllegalArgumentException();
    }

    private boolean isNullOrEmpty(String file) {
        return file == null || file.isEmpty();
    }
}
