package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleCalculateCardHeatRadiator;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCalculateCardHeatRadiatorBase")
public class ModuleCalculateCardHeatRadiatorBase extends ServerModuleBase<ModuleCalculateCardHeatRadiator> {

    public ModuleCalculateCardHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @ZenMethod
    public static ModuleCalculateCardHeatRadiatorBase create(final String registryName) {
        return new ModuleCalculateCardHeatRadiatorBase(registryName);
    }

    @Override
    public ModuleCalculateCardHeatRadiator createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCalculateCardHeatRadiator(server, this, moduleStack.getCount());
    }

}
