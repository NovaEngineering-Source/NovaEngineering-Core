package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleGPU;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleGPUBase")
public class ModuleGPUBase extends ModuleCalculateCardBase<ModuleGPU> {

    public ModuleGPUBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleGPUBase create(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        return new ModuleGPUBase(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleGPU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleGPU(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
