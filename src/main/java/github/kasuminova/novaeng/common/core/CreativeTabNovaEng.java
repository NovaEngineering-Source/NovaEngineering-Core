package github.kasuminova.novaeng.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabNovaEng extends CreativeTabs {
    public static final CreativeTabNovaEng INSTANCE = new CreativeTabNovaEng();

    private CreativeTabNovaEng() {
        super("novaeng_core");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        // TODO Icon...
        return ItemStack.EMPTY;
    }

}
