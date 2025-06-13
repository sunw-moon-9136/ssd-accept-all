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

    private String TestFileName;
    public List<String> testScenarios;

    public TestRunner(String testFileName) {
        TestFileName = testFileName;
        testScenarios = Collections.emptyList();
    }


    public void readTestScriptFile() {

        Path path = Path.of(TestFileName);

        try {
            if (!Files.exists(path)) {
                logger.printConsoleAndLog("TestRunner.readTestScriptFile()", TestFileName + " File not found");
                return;
            }
            testScenarios = Files.readAllLines(path, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.printConsoleAndLog("TestRunner.readTestScriptFile()", TestFileName + " File read Error");

        }
    }

    public void process(IManager manager) {

        readTestScriptFile();

        for (String scnario : testScenarios) {

            ITestScenario testScenario = TestScenarioFactory.getTestScenario(scnario, manager);
            if (testScenario != null) {
                logger.printConsoleAndLog("TestRunner.process()", scnario + " RUN");
                System.out.print(scnario + " --- RUN...");

                String result = testScenario.run() ? "PASS" : "FAIL";
                System.out.println(result);
                logger.printConsoleAndLog("TestRunner.process()", scnario + " RESULT : " + result);

            } else {
                logger.printConsoleAndLog("TestRunner.process()", scnario + " not Found");
            }
        }
    }
}
