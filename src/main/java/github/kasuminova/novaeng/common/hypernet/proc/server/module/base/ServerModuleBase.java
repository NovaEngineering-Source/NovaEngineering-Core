package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class ServerModuleBase<M extends ServerModule> {

    protected final String registryName;
    protected Function<M, List<String>> tooltipFunction = null;

    public ServerModuleBase(final String registryName) {
        this.registryName = registryName;
    }

    public abstract M createInstance(final ModularServer server, final ItemStack moduleStack);

    public List<String> getTooltip(M moduleInstance) {
        return tooltipFunction == null ? Collections.emptyList() : tooltipFunction.apply(moduleInstance);
    }

    public ServerModuleBase<M> setTooltipFunction(final Function<M, List<String>> tooltipFunction) {
        this.tooltipFunction = tooltipFunction;
        return this;
    }

    public String getRegistryName() {
        return registryName;
    }

    @Override
    public int hashCode() {
        return registryName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ServerModuleBase<?> module && registryName.equals(module.registryName);
    }
}
