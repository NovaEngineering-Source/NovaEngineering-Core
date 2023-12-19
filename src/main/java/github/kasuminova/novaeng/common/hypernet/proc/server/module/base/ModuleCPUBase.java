package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPU;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCPUBase")
public class ModuleCPUBase extends ModuleCalculableBase<ModuleCPU> {

    public ModuleCPUBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleCPUBase create(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        return new ModuleCPUBase(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleCPU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCPU(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
