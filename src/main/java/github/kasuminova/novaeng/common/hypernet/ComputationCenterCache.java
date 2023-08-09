package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenterCache")
public class ComputationCenterCache {
    private static ComputationCenterType type = null;
    private static int totalConnected = 0;
    private static float computationPointGeneration = 0;
    private static float computationPointConsumption = 0;

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
    public static float getComputationPointGeneration() {
        return computationPointGeneration;
    }

    public static void setComputationPointGeneration(final float computationPointGeneration) {
        ComputationCenterCache.computationPointGeneration = computationPointGeneration;
    }

    @ZenMethod
    public static float getComputationPointConsumption() {
        return computationPointConsumption;
    }

    public static void setComputationPointConsumption(final float computationPointConsumption) {
        ComputationCenterCache.computationPointConsumption = computationPointConsumption;
    }
}
