package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleExtensionCardExt")
public class ModuleExtensionCardExt extends ServerModule implements HardwareBandwidthConsumer {

    protected int hardwareBandwidth;

    public ModuleExtensionCardExt(final ModularServer server,final ServerModuleBase<?> moduleBase, final int hardwareBandwidth) {
        super(server, moduleBase);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleExtensionCardExt cast(ServerModule module) {
        return module instanceof ModuleExtensionCardExt ? (ModuleExtensionCardExt) module : null;
    }

    @Override
    public int getHardwareBandwidth() {
        return hardwareBandwidth;
    }

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }

}
