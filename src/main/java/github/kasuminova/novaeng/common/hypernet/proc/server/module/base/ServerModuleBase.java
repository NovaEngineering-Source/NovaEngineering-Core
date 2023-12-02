package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;
import net.minecraft.item.ItemStack;

public abstract class ServerModuleBase<M extends ServerModule> {

    protected final String registryName;

    public ServerModuleBase(final String registryName) {
        this.registryName = registryName;
    }

    public abstract M createInstance(final CalculateServer server, final ItemStack moduleStack);

    public String getRegistryName() {
        return registryName;
    }
}
