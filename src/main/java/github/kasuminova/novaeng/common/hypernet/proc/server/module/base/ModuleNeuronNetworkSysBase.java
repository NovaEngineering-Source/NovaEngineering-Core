package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleNeuronNetworkSys;
import net.minecraft.item.ItemStack;

public class ModuleNeuronNetworkSysBase extends ModuleCalculateCardBase<ModuleNeuronNetworkSys> {

    public ModuleNeuronNetworkSysBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleNeuronNetworkSys createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }
}
