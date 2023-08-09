package github.kasuminova.novaeng.common.util;

import java.util.Random;

public class RandomUtils {
    public static final Random RD = new Random();

    public static int nextInt(int bound) {
        return RD.nextInt(bound);
    }

    public static float nextFloat() {
        return RD.nextFloat();
    }
}
