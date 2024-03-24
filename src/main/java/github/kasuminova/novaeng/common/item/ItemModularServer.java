package github.kasuminova.novaeng.common.item;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemModularServer extends Item {

    public ItemModularServer(final String registryName) {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabNovaEng.INSTANCE);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, registryName)).setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + registryName);;
    }

}
