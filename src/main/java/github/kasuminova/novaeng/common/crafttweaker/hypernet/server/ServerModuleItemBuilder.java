package github.kasuminova.novaeng.common.crafttweaker.hypernet.server;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.model.ItemModelFileAutoGenerator;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.item.ItemServerModule;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.io.IOException;

@ZenRegister
@ZenClass("novaeng.hypernet.item.ServerModuleItemBuilder")
public class ServerModuleItemBuilder {

    protected final String registryName;
    protected ServerModuleBase<?> boundedModule = null;
    protected int stackSize = 1;

    public ServerModuleItemBuilder(String registryName) {
        this.registryName = registryName;
    }

    @ZenMethod
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
        RegistryItems.ITEMS_TO_REGISTER_CT.add(new ItemServerModule(registryName, boundedModule).setMaxStackSize(stackSize));
    }


    @ZenMethod
    public void registerWithCustomModelPath(final String modelPath) {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        try {
            ItemModelFileAutoGenerator.generate(resourceManager, modelPath);
        } catch (IOException e) {
            NovaEngineeringCore.log.warn("Failed to auto generate item model resource {}, May cause invalid texture.", modelPath);
            NovaEngineeringCore.log.warn(e);
        }
        RegistryItems.CUSTOM_MODEL_ITEMS_TO_REGISTER_CT.put(modelPath, new ItemServerModule(registryName, boundedModule).setMaxStackSize(stackSize));
    }

}
