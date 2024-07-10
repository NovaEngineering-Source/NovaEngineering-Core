package github.kasuminova.novaeng.common.crafttweaker.hypernet.server;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.model.ItemModelFileAutoGenerator;
import github.kasuminova.novaeng.common.item.ItemModularServer;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.io.IOException;

@ZenRegister
@ZenClass("novaeng.hypernet.item.ModularServerItemBuilder")
public class ModularServerItemBuilder {

    protected final String registryName;

    public ModularServerItemBuilder(String registryName) {
        this.registryName = registryName;
    }

    @ZenMethod
    public static ModularServerItemBuilder create(final String registryName) {
        return new ModularServerItemBuilder(registryName);
    }

    @ZenMethod
    public void register() {
        RegistryItems.ITEMS_TO_REGISTER_CT.add(new ItemModularServer(registryName));
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
        RegistryItems.CUSTOM_MODEL_ITEMS_TO_REGISTER_CT.put(modelPath, new ItemModularServer(registryName));
    }

}
