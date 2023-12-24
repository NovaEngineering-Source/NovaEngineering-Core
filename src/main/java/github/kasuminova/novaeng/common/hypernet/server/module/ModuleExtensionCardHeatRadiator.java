package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleExtensionCardHeatRadiator")
public class ModuleExtensionCardHeatRadiator extends ModuleHeatRadiator {

    public ModuleExtensionCardHeatRadiator(final ModularServer server,final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleExtensionCardHeatRadiator cast(ServerModule module) {
        return module instanceof ModuleExtensionCardHeatRadiator ? (ModuleExtensionCardHeatRadiator) module : null;
    }

}