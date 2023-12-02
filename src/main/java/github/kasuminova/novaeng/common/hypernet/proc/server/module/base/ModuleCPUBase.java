package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPU;
import net.minecraft.item.ItemStack;

public class ModuleCPUBase extends ServerModuleBase<ModuleCPU> {

    public ModuleCPUBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCPU createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
