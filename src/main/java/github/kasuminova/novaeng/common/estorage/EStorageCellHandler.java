package github.kasuminova.novaeng.common.estorage;

import appeng.api.storage.*;
import appeng.api.storage.data.IAEStack;
import appeng.me.storage.BasicCellInventoryHandler;
import github.kasuminova.novaeng.common.item.estorage.EStorageCell;
import net.minecraft.item.ItemStack;

public class EStorageCellHandler implements ICellHandler {
    private static final EStorageCellHandler INSTANCE = new EStorageCellHandler();

    public static EStorageCellHandler getHandler(final ItemStack cell) {
        if (INSTANCE.isCell(cell)) {
            return INSTANCE;
        }
        return null;
    }

    @Override
    public boolean isCell(final ItemStack is) {
        return is.getItem() instanceof EStorageCell<?>;
    }

    @Override
    public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(final ItemStack is, final ISaveProvider host, final IStorageChannel<T> channel) {
        final ICellInventory<T> inv = EStorageCellInventory.createInventory(is, host);
        if (inv == null || inv.getChannel() != channel) {
            return null;
        }
        return new BasicCellInventoryHandler<>(inv, channel);
    }
}
