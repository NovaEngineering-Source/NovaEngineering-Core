package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPUExt;
import net.minecraft.item.ItemStack;

public class ModuleCPUExtBase extends ServerModuleBase<ModuleCPUExt> {

    public ModuleCPUExtBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCPUExt createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
