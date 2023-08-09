package github.kasuminova.novaeng.common.hypernet.upgrade.type;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.type.ProcessorModuleType")
public abstract class ProcessorModuleType {
    protected final int minDurability;
    protected final int maxDurability;
    protected final int energyConsumption;

    public ProcessorModuleType(final int minDurability,
                               final int maxDurability,
                               final int energyConsumption)
    {
        this.minDurability = minDurability;
        this.maxDurability = maxDurability;
        this.energyConsumption = energyConsumption;
    }

    @ZenMethod
    public abstract ProcessorModuleType register(String typeName, String localizedName, int level);

    @ZenGetter("minDurability")
    public int getMinDurability() {
        return minDurability;
    }

    @ZenGetter("maxDurability")
    public int getMaxDurability() {
        return maxDurability;
    }

    @ZenGetter("energyConsumption")
    public int getEnergyConsumption() {
        return energyConsumption;
    }
}
