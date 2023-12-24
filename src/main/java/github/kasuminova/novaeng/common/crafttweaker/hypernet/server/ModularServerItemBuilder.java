package github.kasuminova.novaeng.common.crafttweaker.hypernet.server;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.item.ItemModularServer;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

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
}
