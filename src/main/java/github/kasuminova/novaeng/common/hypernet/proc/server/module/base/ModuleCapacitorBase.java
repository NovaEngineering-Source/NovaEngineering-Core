package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCapacitor;
import net.minecraft.item.ItemStack;

public class ModuleCapacitorBase extends ServerModuleBase<ModuleCapacitor> {
    protected final long maxEnergyCapProvide;
    protected final long maxEnergyConsumptionProvide;

    public ModuleCapacitorBase(final String registryName, long maxEnergyCapProvide, long maxEnergyConsumptionProvide) {
        super(registryName);
        this.maxEnergyCapProvide = maxEnergyCapProvide;
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

    @Override
    public ModuleCapacitor createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCapacitor(server, this, maxEnergyCapProvide, maxEnergyConsumptionProvide);
    }

}
