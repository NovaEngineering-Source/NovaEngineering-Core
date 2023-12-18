package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.*;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ModuleCPU extends ServerModule implements CalculablePowered, HardwareBandwidthConsumer {

    protected double baseGeneration;
    protected double energyConsumeRatio;
    protected int hardwareBandwidth;

    public ModuleCPU(final ModularServer parent, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(parent);
        this.baseGeneration = baseGeneration;
        this.energyConsumeRatio = energyConsumeRatio;
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.INTRICATE || type == CalculateTypes.LOGIC) {
            return 1;
        }
        if (type == CalculateTypes.NEURON) {
            return 0.025;
        }
        if (type == CalculateTypes.QBIT) {
            return 0.001;
        }

        return 0;
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {
        super.readNBT(nbt);
    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {
        super.writeNBT(nbt);
    }

    @Override
    public int getHardwareBandwidth() {
        return 0;
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

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }
}