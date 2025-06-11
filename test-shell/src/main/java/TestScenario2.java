import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestScenario2 extends DefaultTestScenario {

    public static final String WRITE_EXPECT_VALUE = "0xAAAABBBB";

    public TestScenario2(RunCommand runCommand) {
        super(runCommand);
    }

    /*
    제작할 Test Script 2
    LBA 순서를 섞어가며 Write 수행
    • Test Script 이름 : 2_PartialLBAWrite
    Test Script 실행 방법
    • Test Shell 에서 “2_PartialLBAWrite” 라고 입력한다.
                • Test Shell 에서 “2_” 만 입력해도 실행 가능하다.
        Test Scenario


    • Loop는 30회
    • 4번 LBA에 값을 적는다.
                • 0번 LBA에 같은 값을 적는다.
    • 3번 LBA에 같은 값을 적는다.
    • 1번 LBA에 같은 값을 적는다.
    • 2번 LBA에 같은 값을 적는다.
    • LBA 0 ~ 4번, ReadCompare
      */

    public final int repeatCnt = 30;
    public final String[] inputAddressList = {"4", "3", "1", "2"};

    @Override
    public boolean run() {
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < repeatCnt; i++) {
                for (String inputAddress : inputAddressList) {
                    doWriteCmd(inputAddress);
                    results.add(doReadCmdResult(inputAddress));
                }

                for (String inputAddress : inputAddressList) {
                    if (!readCompare(inputAddress, WRITE_EXPECT_VALUE)) return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String doReadCmdResult(String inputAddress) throws IOException, InterruptedException {
        String outputFilePath = "ssd_output.txt";
        String cmd = generateReadCommmand(inputAddress);
        runCommand.execute(cmd);
        return Output.readlines();
//        return Files.readString(Paths.get(outputFilePath));
    }

    private String generateReadCommmand(String addr) {
        return "read".toLowerCase() + " " + addr;
    }

    private void doWriteCmd(String Address) throws IOException, InterruptedException {
        String cmd = generateWriteCommand(Address, WRITE_EXPECT_VALUE);
        runCommand.execute(cmd);
    }

    private String generateWriteCommand(String addr, String value) {
        return "write".toLowerCase() + " " + addr + " " + value;
    }

    private boolean readCompare(String adress, String expect) throws IOException, InterruptedException {
        return doReadCmdResult(adress).equals(expect);
    }
}
