package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleExtensionCardExt;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleExtensionCardExtBase")
public class ModuleExtensionCardExtBase extends ServerModuleBase<ModuleExtensionCardExt> {
    protected final int hardwareBandwidth;

    public ModuleExtensionCardExtBase(final String registryName, final int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleExtensionCardExtBase create(final String registryName, final int hardwareBandwidth) {
        return new ModuleExtensionCardExtBase(registryName, hardwareBandwidth);
    }

    @Override
    public ModuleExtensionCardExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleExtensionCardExt(server, this, hardwareBandwidth);
    }

}
