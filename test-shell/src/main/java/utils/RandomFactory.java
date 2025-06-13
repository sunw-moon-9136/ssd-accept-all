package utils;

import java.util.Random;

public class RandomFactory {
    // Mocking을 위해 static으로 구현하지 않음
    public String getRandomHexValue() {
        Random random = new Random();
        int randomInt = random.nextInt();
        String hexString = String.format("%08x", randomInt).toUpperCase();

        return "0x" + hexString;
    }
}
