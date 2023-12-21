package github.kasuminova.novaeng.common.crafttweaker.hypernet.server;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.item.ItemServerModule;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import stanhebben.zenscript.annotations.ZenMethod;

public class ServerModuleItemBuilder {

    protected final String registryName;
    protected ServerModuleBase<?> boundedModule = null;
    protected int stackSize = 1;

    public ServerModuleItemBuilder(String registryName) {
        this.registryName = registryName;
    }

    public static ServerModuleItemBuilder create(final String registryName) {
        return new ServerModuleItemBuilder(registryName);
    }

    @ZenMethod
    public int getStackSize() {
        return stackSize;
    }

    @ZenMethod
    public ServerModuleItemBuilder setStackSize(final int stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    @ZenMethod
    public ServerModuleBase<?> getBoundedModule() {
        return boundedModule;
    }

    @ZenMethod
    public ServerModuleItemBuilder setBoundedModule(final ServerModuleBase<?> boundedModule) {
        this.boundedModule = boundedModule;
        return this;
    }

    @ZenMethod
    public void register() {
        RegistryItems.registerItem(new ItemServerModule(registryName, boundedModule).setMaxStackSize(stackSize));
    }

}
