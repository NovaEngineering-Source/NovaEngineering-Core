package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenterCache")
public class ComputationCenterCache {
    private static volatile ComputationCenterType type = null;
    private static volatile int totalConnected = 0;
    private static volatile double computationPointGeneration = 0;
    private static volatile double computationPointConsumption = 0;

    @ZenMethod
    public static ComputationCenterType getType() {
        return type;
    }

    public static void setType(final ComputationCenterType type) {
        ComputationCenterCache.type = type;
    }

    @ZenMethod
    public static int getTotalConnected() {
        return totalConnected;
    }

    public static void setTotalConnected(final int totalConnected) {
        ComputationCenterCache.totalConnected = totalConnected;
    }

    @ZenMethod
    public static double getComputationPointGeneration() {
        return computationPointGeneration;
    }

    public static void setComputationPointGeneration(final double computationPointGeneration) {
        ComputationCenterCache.computationPointGeneration = computationPointGeneration;
    }

    @ZenMethod
    public static double getComputationPointConsumption() {
        return computationPointConsumption;
    }

    public static void setComputationPointConsumption(final double computationPointConsumption) {
        ComputationCenterCache.computationPointConsumption = computationPointConsumption;
    }
}
