package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCapacitor;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCapacitorBase")
public class ModuleCapacitorBase extends ServerModuleBase<ModuleCapacitor> {
    protected final long maxEnergyCapProvide;
    protected final long maxEnergyConsumptionProvide;

    public ModuleCapacitorBase(final String registryName, final long maxEnergyCapProvide, final long maxEnergyConsumptionProvide) {
        super(registryName);
        this.maxEnergyCapProvide = maxEnergyCapProvide;
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

    @ZenMethod
    public static ModuleCapacitorBase create(final String registryName, final long maxEnergyCapProvide, final long maxEnergyConsumptionProvide) {
        return new ModuleCapacitorBase(registryName, maxEnergyCapProvide, maxEnergyConsumptionProvide);
    }

    @Override
    public ModuleCapacitor createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleCapacitor(server, this, maxEnergyCapProvide, maxEnergyConsumptionProvide);
    }

}
