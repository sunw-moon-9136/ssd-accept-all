package logger;

import driver.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerTest {

    private static final String NOT_IMPORTANT_METHOD_NAME = "Sender.sendMessageWithData()";
    private static final String NOT_IMPORTANT_LOG_MESSAGE = "send and wait sync";

    Logger logger;

    @Mock
    Driver driver;

    @BeforeEach
    void setUp() {
        logger = new Logger(driver);
    }

    @Test
    void printLogAndConsoleTest() {
        doNothing().when(driver).write(any(), any());

        logger.printLogAndConsole(NOT_IMPORTANT_METHOD_NAME, NOT_IMPORTANT_LOG_MESSAGE);

        verify(driver, only()).write(any(), any());
    }
}