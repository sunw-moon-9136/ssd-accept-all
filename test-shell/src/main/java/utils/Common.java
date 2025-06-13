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
            
                ■ erase [LBA] [SIZE]
                  - Deletes data from the specified LBA for the given SIZE (max 10 entries).
                  - Erased values become 0x00000000.
                  - If the range exceeds bounds, logs "ERROR" to ssd_output.txt.
                  - Example: erase 3 5
            
                ■ erase_range [START_LBA] [END_LBA]
                  - Deletes data from START_LBA to END_LBA (inclusive).
                  - Internally translated into SSD delete command(s).
                  - Example: erase_range 10 20
            
                ■ flush
                  - Executes all commands currently in the SSD Command Buffer.
                  - After execution, the buffer will be cleared.
                  - Example: flush
            
                ■ help
                  - Displays this help message.
            
                ■ exit
                  - Exits the shell.
            """;

    public static boolean helpCommand() {
        System.out.println(HELP_TEXT);
        return true;
    }
}
