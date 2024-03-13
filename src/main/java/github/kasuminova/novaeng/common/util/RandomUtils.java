package github.kasuminova.novaeng.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static boolean nextBool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static double nextFloat(float bound) {
        return ThreadLocalRandom.current().nextFloat() * bound;
    }

    public static float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public static double nextDouble(double bound) {
        return ThreadLocalRandom.current().nextDouble(bound);
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
