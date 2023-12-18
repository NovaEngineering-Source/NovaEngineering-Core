package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAMHeatRadiator;
import net.minecraft.item.ItemStack;

public class ModuleRAMHeatRadiatorBase extends ServerModuleBase<ModuleRAMHeatRadiator> {

    public ModuleRAMHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleRAMHeatRadiator createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleRAMHeatRadiator(server, this, moduleStack.getCount());
    }

}
