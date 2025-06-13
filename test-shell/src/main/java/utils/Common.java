package utils;

public class Common {
    public static final String HELP_TEXT = """
            [Team Name]
               Accept All
            [Member]
               sunw-moon-9136, kimdy003, yeahahahah, CodingJongBot, ariari01, BrownLEE99
            [Command Help]
                ■ read [KEY]
                  - Retrieves the value associated with the specified KEY.
                  - Returns "Not Found" if the key does not exist.
                  - Example: read 3
                ■ write [KEY] [VALUE]
                  - Saves or updates the VALUE for the given KEY.
                  - If the KEY already exists, its value will be overwritten.
                  - Example: write 3 0xAAAAFFFF
                ■ fullread
                  - Displays all stored KEY-VALUE pairs.
                  - Useful for debugging or checking the entire dataset.
                  - Example: fullread
                ■ fullwrite [value]
                  - Automatically writes value random entries into storage.
                  - Typically used for bulk testing or initialization.
                  - Example: fullwrite 0xAAAAFFFF
                ■ exit
                  - Exits the shell.
                ■ help
                  - Displays this help message.
            """;

    public static boolean helpCommand() {
        System.out.println(HELP_TEXT);
        return true;
    }
}
