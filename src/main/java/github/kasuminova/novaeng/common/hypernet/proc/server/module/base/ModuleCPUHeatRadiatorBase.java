package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPUHeatRadiator;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCPUHeatRadiatorBase")
public class ModuleCPUHeatRadiatorBase extends ServerModuleBase<ModuleCPUHeatRadiator> {

    public ModuleCPUHeatRadiatorBase(final String registryName) {
        super(registryName);
    }

    @ZenMethod
    public static ModuleCPUHeatRadiatorBase create(final String registryName) {
        return new ModuleCPUHeatRadiatorBase(registryName);
    }

    @Override
    public ModuleCPUHeatRadiator createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCPUHeatRadiator(server, this, moduleStack.getCount());
    }

}
