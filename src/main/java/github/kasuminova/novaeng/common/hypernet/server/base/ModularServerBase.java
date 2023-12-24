package github.kasuminova.novaeng.common.hypernet.server.base;

import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import net.minecraft.item.ItemStack;

public class ModularServerBase {

    public ModularServer createServer(final TileEntitySynchronized owner, final ItemStack stack) {
        return new ModularServer(owner, stack);
    }

}
