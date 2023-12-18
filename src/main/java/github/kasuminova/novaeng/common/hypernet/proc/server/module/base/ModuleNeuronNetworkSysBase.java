package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleNeuronNetworkSys;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleQuantumBitCalculateSys;
import net.minecraft.item.ItemStack;

public class ModuleNeuronNetworkSysBase extends ModuleCalculateCardBase<ModuleNeuronNetworkSys> {

    public ModuleNeuronNetworkSysBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleNeuronNetworkSys createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleNeuronNetworkSys(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }
}
