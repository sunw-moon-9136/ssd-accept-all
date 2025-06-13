package scenario;

import logger.Logger;
import shell.manager.IManager;
import utils.TestScenarioFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class TestRunner {

    private static final Logger logger = Logger.getInstance();
    public static final String TEST_SCRIPT_TXT_FILE_NAME = "test-script.txt";

    private final String scriptFileName;

    public List<String> getTestScenarios() {
        return testScenarios;
    }

    private List<String> testScenarios;

    // @VisibleForTesting
    public TestRunner() {
        scriptFileName = TEST_SCRIPT_TXT_FILE_NAME;
        testScenarios = Collections.emptyList();
    }

    public TestRunner(String scriptFileName) {
        this.scriptFileName = scriptFileName;
        testScenarios = Collections.emptyList();
    }

    public void readTestScriptFile() {
        Path path = Path.of(scriptFileName);
        try {
            if (!Files.exists(path)) {
                logger.printConsoleAndLog("TestRunner.readTestScriptFile()", scriptFileName + " File not found");
                return;
            }

            testScenarios = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.printConsoleAndLog("TestRunner.readTestScriptFile()", scriptFileName + " File read Error");
        }
    }

    public void process(IManager manager) {
        readTestScriptFile();

        for (String scenario : testScenarios) {
            ITestScenario testScenario = TestScenarioFactory.getTestScenario(scenario, manager);

            if (testScenario == null) {
                logger.printConsoleAndLog("TestRunner.process()", scenario + " not Found");
                return;
            }

            printBeforeRunTestScenario(scenario);
            boolean result = run(testScenario);
            printAfterRunTestScenario(scenario, result);
            if (!result) return;
        }
    }

    // @VisibleForTesting
    boolean run(ITestScenario testScenario) {
        return testScenario.run();
    }

    private void printAfterRunTestScenario(String scenario, boolean result) {
        String resultString = result ? "PASS" : "FAIL";
        System.out.println(resultString);
        logger.printConsoleAndLog("TestRunner.process()", scenario + " RESULT : " + resultString);
    }

    private void printBeforeRunTestScenario(String scnario) {
        logger.printConsoleAndLog("TestRunner.process()", scnario + " RUN");
        System.out.print(scnario + " --- RUN...");
    }
}
