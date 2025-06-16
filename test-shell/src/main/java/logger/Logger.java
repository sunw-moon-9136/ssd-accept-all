package logger;

import driver.Driver;
import driver.FileDriver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String DATE_TIME_FORMAT = "yy.MM.dd HH:mm";
    private static final int TARGET_PREFIX_LENGTH = 35; // 날짜 태그를 제외한 접두사(메소드명 + 패딩 + " : ")의 목표 길이
    private static final String COLON_SEPARATOR = " : "; // 콜론 구분자 및 양쪽 공백
    public static final int REQUIRED_SPACE_FOR_SEPARATOR = COLON_SEPARATOR.length();

    private static final Logger instance = new Logger();
    public static final String LATEST_LOG_FILE_NAME = "latest.log";
    private static final long TEST_LOG_MAX_SIZE = 10 * 1024;
    public static final String LINE_BREAK = "\n";
    public static final String LOG_DIRECTORY_NAME = "log";
    public static final String LATEST_FILE_FULL_PATH = LOG_DIRECTORY_NAME + "/" + LATEST_LOG_FILE_NAME;

    private final Driver fileDriver;

    private Logger() {
        this.fileDriver = new FileDriver();
    }

    // @VisibleForTesting
    Logger(Driver fileDriver) {
        this.fileDriver = fileDriver;
    }

    public static Logger getInstance() {
        return instance;
    }

    public void printConsoleAndLog(String methodFullName, String logMessage) {
        this.fileDriver.createDirectoryIfAbsent(LOG_DIRECTORY_NAME);
        String fullMessage = makeFullMessage(methodFullName, logMessage) + LINE_BREAK;
        printAndManageLogFile(fullMessage);
    }

    private void printAndManageLogFile(String fullMessage) {
        fileDriver.append(LATEST_FILE_FULL_PATH, fullMessage.getBytes(StandardCharsets.UTF_8));
        fileDriver.changeNameIfBiggerThan(TEST_LOG_MAX_SIZE, LATEST_FILE_FULL_PATH, this::makeOldLogFileName);
        fileDriver.changeOldLogFileName(LATEST_LOG_FILE_NAME, LOG_DIRECTORY_NAME);
    }

    private String makeFullMessage(String methodFullName, String logMessage) {
        String dateTimeString = getCurrentFormattedDateTime();
        String paddedPrefix = generatePaddedMethodPrefix(methodFullName);
        return String.format("[%s] %s%s", dateTimeString, paddedPrefix, logMessage);
    }

    private String getCurrentFormattedDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    private String generatePaddedMethodPrefix(String methodFullName) {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.append(methodFullName);

        int paddingLength = TARGET_PREFIX_LENGTH - methodFullName.length() - REQUIRED_SPACE_FOR_SEPARATOR;
        if (paddingLength > 0) {
            prefixBuilder.append(" ".repeat(paddingLength));
        }
        prefixBuilder.append(COLON_SEPARATOR);

        return prefixBuilder.toString();
    }

    // @VisibleForTesting
    Path makeOldLogFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd_HH'h'_mm'm'_ss's'");
        return Path.of(LOG_DIRECTORY_NAME + "/until_" + LocalDateTime.now().format(formatter) + ".log");
    }
}
