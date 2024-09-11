package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleQuantumCalculateCard;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleQuantumCalculateCardBase")
public class ModuleQuantumCalculateCardBase extends ModuleCalculateCardBase<ModuleQuantumCalculateCard> {
    public ModuleQuantumCalculateCardBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleQuantumCalculateCardBase create(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        return new ModuleQuantumCalculateCardBase(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleQuantumCalculateCard createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleQuantumCalculateCard(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }
}
