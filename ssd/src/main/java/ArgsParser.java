public class ArgsParser {
    public static boolean isValidModeCommand(String arg) {
        return (arg.equals("R") || arg.equals("W"));
    }

    public static boolean isValidArgs(String[] args) {
        return isValidModeCommand(args[0]);
    }

    public static void main(String[] args) {
        if(!isValidArgs(args)) throw new RuntimeException();
    }
}
