package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPUHeatRadiator;
import net.minecraft.item.ItemStack;

public class ModuleCPUHeatRadiatorBase extends ServerModuleBase<ModuleCPUHeatRadiator> {

    public ModuleCPUHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCPUHeatRadiator createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
