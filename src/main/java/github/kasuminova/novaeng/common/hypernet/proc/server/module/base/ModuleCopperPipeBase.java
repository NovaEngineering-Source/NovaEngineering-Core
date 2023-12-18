
package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCopperPipe;
import net.minecraft.item.ItemStack;

public class ModuleCopperPipeBase extends ServerModuleBase<ModuleCopperPipe> {

    public ModuleCopperPipeBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleCopperPipe createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCopperPipe(server, this, moduleStack.getCount());
    }

}
