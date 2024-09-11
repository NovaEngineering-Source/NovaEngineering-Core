package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.computer.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCPUExt")
public class ModuleCPUExt extends ServerModule implements HardwareBandwidthConsumer {
    protected int hardwareBandwidth;

    public ModuleCPUExt(final ModularServer server, final ServerModuleBase<?> moduleBase, final int hardwareBandwidth) {
        super(server, moduleBase);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleCPUExt cast(ServerModule module) {
        return module instanceof ModuleCPUExt ? (ModuleCPUExt) module : null;
    }

    @Override
    public int getHardwareBandwidth() {
        return 0;
    }

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }
}
