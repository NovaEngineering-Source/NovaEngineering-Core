package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleCPUExt;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCPUExtBase")
public class ModuleCPUExtBase extends ServerModuleBase<ModuleCPUExt> {
    protected final int hardwareBandwidth;

    public ModuleCPUExtBase(final String registryName, final int hardwareBandwidth) {
        super(registryName);
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @ZenMethod
    public static ModuleCPUExtBase create(final String registryName, final int hardwareBandwidth) {
        return new ModuleCPUExtBase(registryName, hardwareBandwidth);
    }

    @Override
    public ModuleCPUExt createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCPUExt(server, this, hardwareBandwidth);
    }

}
