package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.DataProcessorType")
public class DataProcessorType {
    private final String typeName;
    private final int heatDistribution;
    private final int overheatThreshold;
    private final int circuitDurability;
    private final int minCircuitConsumeAmount;
    private final int maxCircuitConsumeAmount;
    private final float circuitConsumeChance;

    public DataProcessorType(final String typeName,
                             final int heatDistribution,
                             final int overheatThreshold,
                             final int circuitDurability,
                             final int minCircuitConsumeAmount,
                             final int maxCircuitConsumeAmount,
                             final float circuitConsumeChance) {
        this.typeName = typeName;
        this.heatDistribution = heatDistribution;
        this.overheatThreshold = overheatThreshold;
        this.circuitDurability = circuitDurability;
        this.minCircuitConsumeAmount = minCircuitConsumeAmount;
        this.maxCircuitConsumeAmount = maxCircuitConsumeAmount;
        this.circuitConsumeChance = circuitConsumeChance;
    }

    @ZenMethod
    public static DataProcessorType create(final String typeName,
                                           final int heatDistribution,
                                           final int overheatThreshold,
                                           final int circuitDurability,
                                           final int minCircuitConsumeAmount,
                                           final int maxCircuitConsumeAmount,
                                           final float circuitConsumeChance) {
        return new DataProcessorType(typeName,
                heatDistribution,
                overheatThreshold,
                circuitDurability,
                minCircuitConsumeAmount,
                maxCircuitConsumeAmount,
                circuitConsumeChance);
    }

    @ZenGetter("typeName")
    public String getTypeName() {
        return typeName;
    }

    @ZenGetter("heatDistribution")
    public int getHeatDistribution() {
        return heatDistribution;
    }

    @ZenGetter("overheatThreshold")
    public int getOverheatThreshold() {
        return overheatThreshold;
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
