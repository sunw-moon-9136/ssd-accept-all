package scenario;

import shell.Processor;
import shell.output.Output;

public class PartialLBAWrite extends DefaultTestScenario {
    public final int repeatCnt = 30;
    public final String[] inputAddressList = {"4", "0", "3", "1", "2"};
    public static final String WRITE_EXPECT_VALUE = "0xAAAABBBB";

    public PartialLBAWrite(Processor processor, Output output) {
        super(processor, output);
    }

    @Override
    public boolean run() {
        for (int i = 0; i < repeatCnt; i++) {
            if (!isSuccessTestScenarioOnce()) return false;
        }
        return true;
    }

    private boolean isSuccessTestScenarioOnce() {
        try {
            for (String inputAddress : inputAddressList) {
                doWriteCmd(inputAddress);
            }
            for (String inputAddress : inputAddressList) {
                if (!readCompare(Integer.parseInt(inputAddress), WRITE_EXPECT_VALUE)) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void doWriteCmd(String Address) {
        String cmd = generateWriteCommand(Address, WRITE_EXPECT_VALUE);
        processor.execute(cmd);
    }

    private String generateWriteCommand(String writeAddress, String writeValue) {
        return "write".toLowerCase() + " " + writeAddress + " " + writeValue;
    }
}
