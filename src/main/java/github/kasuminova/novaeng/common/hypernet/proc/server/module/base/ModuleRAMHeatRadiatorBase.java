package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAMHeatRadiator;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleRAMHeatRadiatorBase")
public class ModuleRAMHeatRadiatorBase extends ServerModuleBase<ModuleRAMHeatRadiator> {

    public ModuleRAMHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @ZenMethod
    public static ModuleRAMHeatRadiatorBase create(final String registryName) {
        return new ModuleRAMHeatRadiatorBase(registryName);
    }

    @Override
    public ModuleRAMHeatRadiator createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleRAMHeatRadiator(server, this, moduleStack.getCount());
    }

}
