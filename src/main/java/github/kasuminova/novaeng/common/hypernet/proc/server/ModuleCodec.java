package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModuleCodec<T extends ServerModule> {

    @Nonnull
    String getTypeName();

    @Nullable
    T readNBT(@Nonnull NBTTagCompound nbt);

    @Nonnull
    NBTTagCompound writeNBT(@Nonnull T module);

}