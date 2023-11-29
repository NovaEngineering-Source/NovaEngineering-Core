package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.Extension;
import github.kasuminova.novaeng.common.hypernet.proc.server.ServerModule;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ModuleExtCard extends ServerModule implements Extension {

    public ModuleExtCard(final CalculateServer parent) {
        super(parent);
    }

    @Override
    public void readNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void writeNBT(@Nonnull final NBTTagCompound nbt) {

    }

    @Override
    public void onCalculate(final CalculateRequest request) {

    }
}