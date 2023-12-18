package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

@ZenRegister
@ZenClass("novaeng.hypernet.module.ModuleCapacitor")
public class ModuleCapacitor extends ServerModule {

    protected long maxEnergyCapProvide;
    protected long maxEnergyConsumptionProvide;

    public ModuleCapacitor(final ModularServer server,final ServerModuleBase<?> moduleBase, final long maxEnergyCapProvide, final long maxEnergyConsumptionProvide) {
        super(server, moduleBase);
        this.maxEnergyCapProvide = maxEnergyCapProvide;
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

    @ZenMethod
    public static ModuleCapacitor cast(ServerModule module) {
        return module instanceof ModuleCapacitor ? (ModuleCapacitor) module : null;
    }

    public long getMaxEnergyCapProvide() {
        return maxEnergyCapProvide;
    }

    public void setMaxEnergyCapProvide(final long maxEnergyCapProvide) {
        this.maxEnergyCapProvide = maxEnergyCapProvide;
    }

    public long getMaxEnergyConsumptionProvide() {
        return maxEnergyConsumptionProvide;
    }

    public void setMaxEnergyConsumptionProvide(final long maxEnergyConsumptionProvide) {
        this.maxEnergyConsumptionProvide = maxEnergyConsumptionProvide;
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

}
