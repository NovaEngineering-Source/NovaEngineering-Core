package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModulePSU;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModulePSUBase")
public class ModulePSUBase extends ServerModuleBase<ModulePSU> {
    protected final long maxEnergyProvision;

    public ModulePSUBase(final String registryName, final long maxEnergyProvision) {
        super(registryName);
        this.maxEnergyProvision = maxEnergyProvision;
    }

    @ZenMethod
    public static ModulePSUBase create(final String registryName, final long maxEnergyProvision) {
        return new ModulePSUBase(registryName, maxEnergyProvision);
    }

    @Override
    public ModulePSU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModulePSU(server, this, maxEnergyProvision);
    }

}
