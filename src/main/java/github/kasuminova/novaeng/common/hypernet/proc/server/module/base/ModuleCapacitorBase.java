package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCapacitor;
import net.minecraft.item.ItemStack;

public class ModuleCapacitorBase extends ServerModuleBase<ModuleCapacitor> {

    public ModuleCapacitorBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCapacitor createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
