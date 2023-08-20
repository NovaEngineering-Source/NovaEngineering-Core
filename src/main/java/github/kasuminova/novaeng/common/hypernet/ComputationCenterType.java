package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenterType")
public class ComputationCenterType {
    private final String typeName;
    private final int maxConnections;
    private final float maxComputationPointCarrying;
    private final int circuitDurability;
    private final int minCircuitConsumeAmount;
    private final int maxCircuitConsumeAmount;
    private final float circuitConsumeChance;

    public ComputationCenterType(final String typeName,
                                 final int maxConnections,
                                 final float maxComputationPointCarrying,
                                 final int circuitDurability,
                                 final int minCircuitConsumeAmount,
                                 final int maxCircuitConsumeAmount,
                                 final float circuitConsumeChance) {
        this.typeName = typeName;
        this.maxConnections = maxConnections;
        this.maxComputationPointCarrying = maxComputationPointCarrying;
        this.circuitDurability = circuitDurability;
        this.minCircuitConsumeAmount = minCircuitConsumeAmount;
        this.maxCircuitConsumeAmount = maxCircuitConsumeAmount;
        this.circuitConsumeChance = circuitConsumeChance;
    }

    @ZenMethod
    public static ComputationCenterType create(final String typeName,
                                               final int maxConnections,
                                               final float maxComputationPointCarrying,
                                               final int circuitDurability,
                                               final int minCircuitConsumeAmount,
                                               final int maxCircuitConsumeAmount,
                                               final float circuitConsumeChance) {
        return new ComputationCenterType(typeName,
                maxConnections,
                maxComputationPointCarrying,
                circuitDurability,
                minCircuitConsumeAmount,
                maxCircuitConsumeAmount,
                circuitConsumeChance);
    }

    @ZenGetter("typeName")
    public String getTypeName() {
        return typeName;
    }

    @ZenGetter("maxConnections")
    public int getMaxConnections() {
        return maxConnections;
    }

    @ZenGetter("maxComputationPointCarrying")
    public float getMaxComputationPointCarrying() {
        return maxComputationPointCarrying;
    }

    @ZenGetter("circuitDurability")
    public int getCircuitDurability() {
        return circuitDurability;
    }

    @ZenGetter("minCircuitConsumeAmount")
    public int getMinCircuitConsumeAmount() {
        return minCircuitConsumeAmount;
    }

    @ZenGetter("maxCircuitConsumeAmount")
    public int getMaxCircuitConsumeAmount() {
        return maxCircuitConsumeAmount;
    }

    @ZenGetter("circuitConsumeChance")
    public float getCircuitConsumeChance() {
        return circuitConsumeChance;
    }
}
