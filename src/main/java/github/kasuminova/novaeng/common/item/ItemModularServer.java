package github.kasuminova.novaeng.common.item;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.core.CreativeTabHyperNet;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemModularServer extends Item {

    public ItemModularServer() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabHyperNet.INSTANCE);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "modular_server"));
    }

}
