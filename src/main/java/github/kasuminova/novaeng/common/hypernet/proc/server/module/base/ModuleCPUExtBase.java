package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPUExt;
import net.minecraft.item.ItemStack;

public class ModuleCPUExtBase extends ServerModuleBase<ModuleCPUExt> {
    protected final int hardwareBandwidth;

    public ModuleCPUExtBase(final String registryName, int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public ModuleCPUExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCPUExt(server, this, hardwareBandwidth);
    }

}
