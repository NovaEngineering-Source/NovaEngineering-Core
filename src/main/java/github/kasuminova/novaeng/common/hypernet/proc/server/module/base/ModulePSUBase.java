package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModulePSU;
import net.minecraft.item.ItemStack;

public class ModulePSUBase extends ServerModuleBase<ModulePSU> {
    protected final long maxEnergyProvision;

    public ModulePSUBase(final String registryName, long maxEnergyProvision) {
        super(registryName);
        this.maxEnergyProvision = maxEnergyProvision;
    }

    @Override
    public ModulePSU createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModulePSU(server, this, maxEnergyProvision);
    }

}
