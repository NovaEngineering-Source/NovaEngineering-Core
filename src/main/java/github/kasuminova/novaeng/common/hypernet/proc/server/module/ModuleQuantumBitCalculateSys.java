package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.Calculable;
import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ServerModule;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ModuleQuantumBitCalculateSys extends ServerModule implements Calculable {

    public ModuleQuantumBitCalculateSys(final CalculateServer parent) {
        super(parent);
    }

    @Override
    public double calculate(final CalculateRequest request) {
        return 0;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.QBIT) {
            return 1;
        }
        if (type == CalculateTypes.INTRICATE) {
            return 0.5;
        }
        if (type == CalculateTypes.NEURON) {
            return 0.3;
        }
        if (type == CalculateTypes.LOGIC) {
            return 0.25;
        }

        return 0;
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

}