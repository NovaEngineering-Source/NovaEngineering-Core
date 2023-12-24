package github.kasuminova.novaeng.common.hypernet.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.ModuleExtensionCardHeatRadiator;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleExtensionCardHeatRadiatorBase")
public class ModuleExtensionCardHeatRadiatorBase extends ServerModuleBase<ModuleExtensionCardHeatRadiator> {

    public ModuleExtensionCardHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @ZenMethod
    public static ModuleExtensionCardHeatRadiatorBase create(final String registryName) {
        return new ModuleExtensionCardHeatRadiatorBase(registryName);
    }

    @Override
    public ModuleExtensionCardHeatRadiator createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleExtensionCardHeatRadiator(server, this, moduleStack.getCount());
    }

}
