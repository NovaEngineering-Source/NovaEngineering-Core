package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModulePSU;
import net.minecraft.item.ItemStack;

public class ModulePSUBase extends ServerModuleBase<ModulePSU> {

    public ModulePSUBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModulePSU createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
