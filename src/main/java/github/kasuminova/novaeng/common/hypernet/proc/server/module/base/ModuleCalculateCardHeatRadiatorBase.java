package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCardHeatRadiator;
import net.minecraft.item.ItemStack;

public class ModuleCalculateCardHeatRadiatorBase extends ServerModuleBase<ModuleCalculateCardHeatRadiator> {

    public ModuleCalculateCardHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCalculateCardHeatRadiator createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
