package github.kasuminova.novaeng.common.item;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemServerModule extends Item {

    protected final ServerModuleBase<?> boundedModule;

    public ItemServerModule(final String registryName, final ServerModuleBase<?> boundedModule) {
        setCreativeTab(CreativeTabNovaEng.INSTANCE);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, registryName)).setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + registryName);
        this.boundedModule = boundedModule;
    }

    public ItemServerModule(final String registryName) {
        this(registryName, null);
    }

    public ServerModuleBase<?> getBoundedModule() {
        return boundedModule;
    }
}
