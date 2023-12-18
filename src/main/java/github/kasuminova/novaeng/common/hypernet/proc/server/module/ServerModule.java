package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public abstract class ServerModule {

    protected final ModularServer server;

    protected boolean broken;

    public ServerModule(final ModularServer server) {
        this.server = server;
    }

    public ModularServer getServer() {
        return server;
    }

    public void readNBT(@Nonnull NBTTagCompound nbt) {
        broken = nbt.getBoolean("broken");
    }

    public void writeNBT(@Nonnull NBTTagCompound nbt) {
        if (broken) {
            nbt.setBoolean("broken", true);
        }
    }

}
