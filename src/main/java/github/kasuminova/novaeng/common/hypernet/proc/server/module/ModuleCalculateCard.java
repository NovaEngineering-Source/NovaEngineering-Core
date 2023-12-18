package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.server.*;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public abstract class ModuleCalculateCard extends ServerModule implements CalculablePowered, HardwareBandwidthConsumer {
    protected double baseGeneration;
    protected double energyConsumeRatio;

    public ModuleCalculateCard(final ModularServer parent) {
        super(parent);
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public double getBaseGeneration() {
        return baseGeneration;
    }

    public void setBaseGeneration(final double baseGeneration) {
        this.baseGeneration = baseGeneration;
    }

    @Override
    public double getEnergyConsumeRatio() {
        return energyConsumeRatio;
    }

    public void setEnergyConsumeRatio(final double energyConsumeRatio) {
        this.energyConsumeRatio = energyConsumeRatio;
    }

}
