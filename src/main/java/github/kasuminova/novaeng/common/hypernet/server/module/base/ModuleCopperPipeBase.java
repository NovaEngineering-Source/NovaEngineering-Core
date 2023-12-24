
package github.kasuminova.novaeng.common.hypernet.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.ModuleCopperPipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCopperPipeBase")
public class ModuleCopperPipeBase extends ServerModuleBase<ModuleCopperPipe> {

    public ModuleCopperPipeBase(final String registryName) {
        super(registryName);
    }

    @ZenMethod
    public static ModuleCopperPipeBase create(final String registryName) {
        return new ModuleCopperPipeBase(registryName);
    }

    @Override
    public ModuleCopperPipe createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCopperPipe(server, this, moduleStack.getCount());
    }

}
