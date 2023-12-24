package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCalculateCardExt")
public class ModuleCalculateCardExt extends ServerModule implements HardwareBandwidthConsumer {
    protected int hardwareBandwidth;

    public ModuleCalculateCardExt(final ModularServer server,final ServerModuleBase<?> moduleBase, final int hardwareBandwidth) {
        super(server, moduleBase);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleCalculateCardExt cast(ServerModule module) {
        return module instanceof ModuleCalculateCardExt ? (ModuleCalculateCardExt) module : null;
    }

    @Override
    public int getHardwareBandwidth() {
        return hardwareBandwidth;
    }

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }
}
