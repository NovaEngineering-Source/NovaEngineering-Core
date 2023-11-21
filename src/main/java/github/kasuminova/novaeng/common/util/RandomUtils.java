package github.kasuminova.novaeng.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt();
    }

    public static float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }
}
