package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.Calculable;
import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ServerModule;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ModuleCPU extends ServerModule implements Calculable {

    public ModuleCPU(final CalculateServer parent) {
        super(parent);
    }

    @Override
    public double calculate(final CalculateRequest request) {
        return 0;
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

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

}