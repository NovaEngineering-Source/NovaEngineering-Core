package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAMHeatRadiator;
import net.minecraft.item.ItemStack;

public class ModuleRAMHeatRadiatorBase extends ServerModuleBase<ModuleRAMHeatRadiator> {

    public ModuleRAMHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleRAMHeatRadiator createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
