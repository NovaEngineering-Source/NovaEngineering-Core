package github.kasuminova.novaeng.common.hypernet.proc.server;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public abstract class ServerModule {

    protected final CalculateServer parent;

    public ServerModule(final CalculateServer parent) {
        this.parent = parent;
    }

    public CalculateServer getParent() {
        return parent;
    }

    public abstract void readNBT(@Nonnull NBTTagCompound nbt);

    public abstract void writeNBT(@Nonnull NBTTagCompound nbt);

}
