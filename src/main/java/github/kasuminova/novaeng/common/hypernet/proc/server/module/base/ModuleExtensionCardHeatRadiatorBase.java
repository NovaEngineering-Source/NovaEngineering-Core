package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleExtensionCardHeatRadiator;
import net.minecraft.item.ItemStack;

public class ModuleExtensionCardHeatRadiatorBase extends ServerModuleBase<ModuleExtensionCardHeatRadiator> {

    public ModuleExtensionCardHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleExtensionCardHeatRadiator createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
