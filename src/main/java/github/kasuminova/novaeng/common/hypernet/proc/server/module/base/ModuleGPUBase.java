package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleGPU;
import net.minecraft.item.ItemStack;

public class ModuleGPUBase extends ModuleCalculateCardBase<ModuleGPU> {

    public ModuleGPUBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleGPU createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
