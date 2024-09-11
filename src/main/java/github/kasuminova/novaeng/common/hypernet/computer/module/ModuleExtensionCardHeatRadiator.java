package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleExtensionCardHeatRadiator")
public class ModuleExtensionCardHeatRadiator extends ModuleHeatRadiator {

    public ModuleExtensionCardHeatRadiator(final ModularServer server, final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleExtensionCardHeatRadiator cast(ServerModule module) {
        return module instanceof ModuleExtensionCardHeatRadiator ? (ModuleExtensionCardHeatRadiator) module : null;
    }

}