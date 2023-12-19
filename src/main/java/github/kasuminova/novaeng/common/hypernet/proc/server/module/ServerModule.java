package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;

import javax.annotation.Nonnull;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ServerModule")
public abstract class ServerModule {

    protected final ModularServer server;
    protected final ServerModuleBase<?> moduleBase;

    protected boolean broken;

    public ServerModule(final ModularServer server,final ServerModuleBase<?> moduleBase) {
        this.server = server;
        this.moduleBase = moduleBase;
    }

    public ModularServer getServer() {
        return server;
    }

    public ServerModuleBase<?> getModuleBase() {
        return moduleBase;
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
