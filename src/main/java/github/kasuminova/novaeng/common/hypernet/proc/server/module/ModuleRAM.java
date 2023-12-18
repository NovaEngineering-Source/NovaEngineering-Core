package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.HardwareBandwidthProvider;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ModuleRAM extends ServerModule implements HardwareBandwidthProvider {

    public ModuleRAM(final ModularServer parent) {
        super(parent);
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public int getHardwareBandwidthProvision() {
        return 0;
    }
}