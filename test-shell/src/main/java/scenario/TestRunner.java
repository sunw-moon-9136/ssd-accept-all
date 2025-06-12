package scenario;

import shell.manager.IManager;
import utils.TestScenarioFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class TestRunner {
    private String TestFileName;
    public List<String> testScenarios;

    public TestRunner(String testFileName) {
        TestFileName = testFileName;
        testScenarios = Collections.emptyList();
    }


    public void readTestScriptFile() {

        Path path = Paths.get(TestFileName);

        try {
            if (!Files.exists(path)) {
                System.out.println("file 없음");
                return;
            }
            testScenarios = Files.readAllLines(path, StandardCharsets.UTF_8);

        } catch (Exception e) {
            //To-do
            System.out.println("오류");
        }
    }

    public void process(IManager manager) {

        readTestScriptFile();

        for (String scnario : testScenarios) {
            // Test Scenario
            ITestScenario testScenario = TestScenarioFactory.getTestScenario(scnario, manager);
            if (testScenario != null) {
                System.out.print(scnario + " --- RUN...");
                String result = testScenario.run() ? "PASS" : "FAIL";
                System.out.println(result);

            }
        }
    }
}
