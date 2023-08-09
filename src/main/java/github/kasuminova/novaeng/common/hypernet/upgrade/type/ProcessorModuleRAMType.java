package github.kasuminova.novaeng.common.hypernet.upgrade.type;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.mmce.common.upgrade.registry.RegistryUpgrade;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleRAM;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.type.ProcessorModuleRAMType")
public class ProcessorModuleRAMType extends ProcessorModuleType {
    private final float computationPointGenerationLimit;

    public ProcessorModuleRAMType(final int minDurability,
                                  final int maxDurability,
                                  final int energyConsumption,
                                  final float computationPointGenerationLimit)
    {
        super(minDurability, maxDurability, energyConsumption);
        this.computationPointGenerationLimit = computationPointGenerationLimit;
    }

    @ZenMethod
    public static ProcessorModuleRAMType create(final int minDurability,
                                                final int maxDurability,
                                                final int energyConsumption,
                                                final float computationPointGenerationLimit)
    {
        return new ProcessorModuleRAMType(minDurability, maxDurability, energyConsumption, computationPointGenerationLimit);
    }

    @Override
    public ProcessorModuleRAMType register(String typeName, String localizedName, int level) {
        UpgradeType type = new UpgradeType(typeName, localizedName, level, 1);
        RegistryHyperNet.addDataProcessorModuleRAMType(type, this);
        RegistryUpgrade.registerUpgrade(typeName, new ProcessorModuleRAM(type));

        return this;
    }

    @ZenGetter("computationPointGenerationLimit")
    public float getComputationPointGenerationLimit() {
        return computationPointGenerationLimit;
    }
}
