package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCopperPipe")
public class ModuleCopperPipe extends ModuleHeatRadiator {

    public ModuleCopperPipe(final ModularServer server,final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleCopperPipe cast(ServerModule module) {
        return module instanceof ModuleCopperPipe ? (ModuleCopperPipe) module : null;
    }

}
