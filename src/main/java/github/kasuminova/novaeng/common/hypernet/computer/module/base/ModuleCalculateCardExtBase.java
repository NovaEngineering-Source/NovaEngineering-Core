package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleCalculateCardExt;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCalculateCardExtBase")
public class ModuleCalculateCardExtBase extends ServerModuleBase<ModuleCalculateCardExt> {

    protected final int hardwareBandwidth;

    public ModuleCalculateCardExtBase(final String registryName, int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleCalculateCardExtBase create(final String registryName, final int hardwareBandwidth) {
        return new ModuleCalculateCardExtBase(registryName, hardwareBandwidth);
    }

    @Override
    public ModuleCalculateCardExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCalculateCardExt(server, this, hardwareBandwidth);
    }

}
