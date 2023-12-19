package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.HardwareBandwidthProvider;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleRAM")
public class ModuleRAM extends ServerModule implements HardwareBandwidthProvider {

    protected int hardwareBandwidthProvision;

    public ModuleRAM(final ModularServer server,final ServerModuleBase<?> moduleBase, final int hardwareBandwidthProvision) {
        super(server, moduleBase);
        this.hardwareBandwidthProvision = hardwareBandwidthProvision;
    }

    @ZenMethod
    public static ModuleRAM cast(ServerModule module) {
        return module instanceof ModuleRAM ? (ModuleRAM) module : null;
    }

    @Override
    public int getHardwareBandwidthProvision() {
        return hardwareBandwidthProvision;
    }
}