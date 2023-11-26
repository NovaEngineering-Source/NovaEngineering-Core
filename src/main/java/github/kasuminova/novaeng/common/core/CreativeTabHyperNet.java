package github.kasuminova.novaeng.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabHyperNet extends CreativeTabs {
    public static final CreativeTabHyperNet INSTANCE = new CreativeTabHyperNet();

    private CreativeTabHyperNet() {
        super("HyperNet");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        // TODO Icon...
        return ItemStack.EMPTY;
    }
}
