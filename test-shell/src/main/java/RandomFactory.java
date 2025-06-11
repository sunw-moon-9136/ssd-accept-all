import java.util.Random;

public class RandomFactory {
    public String getRandomHexValue() {
        Random random = new Random();
        int randomInt = random.nextInt();
        String hexString = String.format("%08x", randomInt).toUpperCase();

        return "0x" + hexString;
    }
}
