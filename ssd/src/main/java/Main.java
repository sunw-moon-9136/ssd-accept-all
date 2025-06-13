import NAND.DefaultSsdOperator;
import NAND.NandFileDriver;
import NAND.ReadWritable;
import SSD.InputFileHandler;
import SSD.OutputFileHandler;

public class Main {
    public static void main(String[] args) {
        ReadWritable ssd = DefaultSsdOperator.builder()
                .nandDriver(new NandFileDriver())
                .build();

        SsdManager manager = SsdManager.builder()
                .withSsd(ssd)
                .withInputHandler(new InputFileHandler())
                .withOutputHandler(new OutputFileHandler())
                .build();

        manager.run(args);
    }
}
