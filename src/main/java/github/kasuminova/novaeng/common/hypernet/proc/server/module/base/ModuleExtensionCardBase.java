package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleExtensionCard;
import net.minecraft.item.ItemStack;

public class ModuleExtensionCardBase extends ServerModuleBase<ModuleExtensionCard> {

    public ModuleExtensionCardBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleExtensionCard createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
