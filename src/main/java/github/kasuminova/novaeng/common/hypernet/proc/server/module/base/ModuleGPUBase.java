package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleGPU;
import net.minecraft.item.ItemStack;

public class ModuleGPUBase extends ModuleCalculateCardBase<ModuleGPU> {

    public ModuleGPUBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleGPU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleGPU(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
