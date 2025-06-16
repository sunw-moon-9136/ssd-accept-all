package logger;

import driver.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
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
    void printConsoleAndLog() {
        doNothing().when(driver).append(any(), any());

        logger.printConsoleAndLog(NOT_IMPORTANT_METHOD_NAME, NOT_IMPORTANT_LOG_MESSAGE);

        verify(driver, times(1)).append(any(), any());
        verify(driver, times(1)).changeNameIfBiggerThan(anyLong(), anyString(), any());
        verify(driver, times(1)).changeOldLogFileName(Logger.LATEST_FILE_FULL_PATH, Logger.LOG_DIRECTORY_NAME);
    }

    @Test
    void makeOldLogFileName() {
        Path path = logger.makeOldLogFileName();

        assertThat(path.toString()).matches(".*until_\\d{6}_\\d{2}h_\\d{2}m_\\d{2}s\\.log$");
    }
}