package github.kasuminova.novaeng.mixin.util;

import net.minecraft.item.ItemStack;

public interface CachedItemConduit {

    ItemStack getCachedStack();

    void setCachedStack(final ItemStack cachedStack);

}
