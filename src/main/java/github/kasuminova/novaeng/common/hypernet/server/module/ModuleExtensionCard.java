package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.Extension;
import github.kasuminova.novaeng.common.hypernet.server.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleExtensionCard")
public abstract class ModuleExtensionCard extends ServerModule implements HardwareBandwidthConsumer, Extension {

    protected int hardwareBandwidth;

    public ModuleExtensionCard(final ModularServer server,final ServerModuleBase<?> moduleBase) {
        super(server, moduleBase);
    }

    @Override
    public int getHardwareBandwidth() {
        return hardwareBandwidth;
    }

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }

}
