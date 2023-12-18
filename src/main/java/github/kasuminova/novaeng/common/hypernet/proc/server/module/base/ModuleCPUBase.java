package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPU;
import net.minecraft.item.ItemStack;

public class ModuleCPUBase extends ModuleCalculableBase<ModuleCPU> {

    public ModuleCPUBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleCPU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCPU(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
