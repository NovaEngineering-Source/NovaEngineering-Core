package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCardExt;
import net.minecraft.item.ItemStack;

public class ModuleCalculateCardExtBase extends ServerModuleBase<ModuleCalculateCardExt> {

    public ModuleCalculateCardExtBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCalculateCardExt createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
