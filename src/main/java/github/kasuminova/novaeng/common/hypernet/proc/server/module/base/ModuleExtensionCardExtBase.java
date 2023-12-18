package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleExtensionCardExt;
import net.minecraft.item.ItemStack;

public class ModuleExtensionCardExtBase extends ServerModuleBase<ModuleExtensionCardExt> {
    protected final int hardwareBandwidth;

    public ModuleExtensionCardExtBase(final String registryName, int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public ModuleExtensionCardExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleExtensionCardExt(server, this, hardwareBandwidth);
    }

}
