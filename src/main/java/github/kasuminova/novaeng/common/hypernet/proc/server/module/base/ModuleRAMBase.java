package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAM;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleRAMBase")
public class ModuleRAMBase extends ServerModuleBase<ModuleRAM> {
    protected final int hardwareBandwidthProvision;

    public ModuleRAMBase(final String registryName, final int hardwareBandwidthProvision) {
        super(registryName);
        this.hardwareBandwidthProvision = hardwareBandwidthProvision;
    }

    @ZenMethod
    public static ModuleRAMBase create(final String registryName, final int hardwareBandwidthProvision) {
        return new ModuleRAMBase(registryName, hardwareBandwidthProvision);
    }

    @Override
    public ModuleRAM createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleRAM(server, this, hardwareBandwidthProvision);
    }

}
