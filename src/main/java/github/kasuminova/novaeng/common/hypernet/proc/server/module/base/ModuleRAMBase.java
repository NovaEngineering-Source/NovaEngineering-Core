package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAM;
import net.minecraft.item.ItemStack;

public class ModuleRAMBase extends ServerModuleBase<ModuleRAM> {

    public ModuleRAMBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleRAM createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
