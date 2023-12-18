package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.module.ModuleCPUHeatRadiator")
public class ModuleCPUHeatRadiator extends ModuleHeatRadiator {

    public ModuleCPUHeatRadiator(final ModularServer server,final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleCPUHeatRadiator cast(ServerModule module) {
        return module instanceof ModuleCPUHeatRadiator ? (ModuleCPUHeatRadiator) module : null;
    }

}