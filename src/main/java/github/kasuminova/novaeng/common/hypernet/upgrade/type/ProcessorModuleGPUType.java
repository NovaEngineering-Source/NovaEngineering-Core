package github.kasuminova.novaeng.common.hypernet.upgrade.type;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.mmce.common.upgrade.registry.RegistryUpgrade;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleGPU;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.type.ProcessorModuleGPUType")
public class ProcessorModuleGPUType extends ProcessorModuleCPUType {
    public ProcessorModuleGPUType(final int minDurability,
                                  final int maxDurability,
                                  final int energyConsumption,
                                  final double computationPointGeneration)
    {
        super(minDurability, maxDurability, energyConsumption, computationPointGeneration);
    }

    @ZenMethod
    public static ProcessorModuleGPUType createGPUType(final int minDurability,
                                                final int maxDurability,
                                                final int energyConsumption,
                                                final double computationPointGeneration)
    {
        return new ProcessorModuleGPUType(minDurability, maxDurability, energyConsumption, computationPointGeneration);
    }

    @Override
    public ProcessorModuleGPUType register(String typeName, String localizedName, int level) {
        UpgradeType type = new UpgradeType(typeName, localizedName, level, 1);
        RegistryHyperNet.addDataProcessorModuleCPUType(type, this);
        RegistryUpgrade.registerUpgrade(typeName, new ProcessorModuleGPU(type));

        return this;
    }
}
