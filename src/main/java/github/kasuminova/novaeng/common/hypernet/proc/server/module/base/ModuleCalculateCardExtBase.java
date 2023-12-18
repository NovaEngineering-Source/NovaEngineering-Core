package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCardExt;
import net.minecraft.item.ItemStack;

public class ModuleCalculateCardExtBase extends ServerModuleBase<ModuleCalculateCardExt> {

    protected final int hardwareBandwidth;

    public ModuleCalculateCardExtBase(final String registryName, int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public ModuleCalculateCardExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCalculateCardExt(server, this, hardwareBandwidth);
    }

}
