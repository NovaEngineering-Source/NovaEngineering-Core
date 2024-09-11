package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCapacitor")
public class ModuleCapacitor extends ServerModule {

    protected long maxEnergyCapProvide;
    protected long maxEnergyConsumptionProvide;

    public ModuleCapacitor(final ModularServer server, final ServerModuleBase<?> moduleBase, final long maxEnergyCapProvide, final long maxEnergyConsumptionProvide) {
        super(server, moduleBase);
        this.maxEnergyCapProvide = maxEnergyCapProvide;
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

    @ZenMethod
    public static ModuleCapacitor cast(ServerModule module) {
        return module instanceof ModuleCapacitor ? (ModuleCapacitor) module : null;
    }

    public long getMaxEnergyCapProvision() {
        return maxEnergyCapProvide;
    }

    public void setMaxEnergyCapProvide(final long maxEnergyCapProvide) {
        this.maxEnergyCapProvide = maxEnergyCapProvide;
    }

    public long getMaxEnergyConsumptionProvision() {
        return maxEnergyConsumptionProvide;
    }

    public void setMaxEnergyConsumptionProvide(final long maxEnergyConsumptionProvide) {
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

}
