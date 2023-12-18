package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleQuantumBitCalculateSys;
import net.minecraft.item.ItemStack;

public class ModuleQuantumBitCalculateSysBase extends ModuleCalculateCardBase<ModuleQuantumBitCalculateSys> {
    public ModuleQuantumBitCalculateSysBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleQuantumBitCalculateSys createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleQuantumBitCalculateSys(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }
}
