package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCalculateCardHeatRadiator")
public class ModuleCalculateCardHeatRadiator extends ModuleHeatRadiator {

    public ModuleCalculateCardHeatRadiator(final ModularServer server, final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleCalculateCardHeatRadiator cast(ServerModule module) {
        return module instanceof ModuleCalculateCardHeatRadiator ? (ModuleCalculateCardHeatRadiator) module : null;
    }

}