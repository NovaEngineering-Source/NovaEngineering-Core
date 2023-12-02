
package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCopperPipe;
import net.minecraft.item.ItemStack;

public class ModuleCopperPipeBase extends ServerModuleBase<ModuleCopperPipe> {

    public ModuleCopperPipeBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCopperPipe createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }

}
