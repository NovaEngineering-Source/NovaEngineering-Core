package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleRAMHeatRadiator")
public class ModuleRAMHeatRadiator extends ModuleHeatRadiator {

    public ModuleRAMHeatRadiator(final ModularServer server,final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleRAMHeatRadiator cast(ServerModule module) {
        return module instanceof ModuleRAMHeatRadiator ? (ModuleRAMHeatRadiator) module : null;
    }

}