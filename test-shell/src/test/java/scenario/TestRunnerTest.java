package scenario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shell.Processor;
import shell.manager.IManager;
import shell.manager.Manager;
import shell.output.Output;
import utils.LogFileDeleter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TestRunnerTest {

    TestRunner T;
    @Mock
    IManager mockManger;

    @Mock
    Output mockOutput;

    @Mock
    Processor mockProcessor;

    @BeforeEach
    void setUp() {
        mockManger = new Manager(mockProcessor, mockOutput);
    }

    @AfterEach
    void cleanUp() throws IOException {
        LogFileDeleter.deleteRecursivelyLogDirectory();
    }

    @Test
    void testscript_파일가져오는지_확인() {
        T = new TestRunner("C:\\Users\\User\\Documents\\test.txt");
        T.readTestScriptFile();
        int expected = 2;
        int act = T.testScenarios.size();
        for (String o : T.testScenarios) {
            System.out.println(o);
        }
        assertEquals(act, expected);

    }

    @Test
    void TestScript실행하는지확인() {
        T = new TestRunner("C:\\Users\\User\\Documents\\test.txt");
        T.readTestScriptFile();
        T.process(mockManger);
        //verify(T,times(2)).()

    }


}