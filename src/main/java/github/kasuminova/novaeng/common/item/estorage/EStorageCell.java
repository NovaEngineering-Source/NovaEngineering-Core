package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.data.IAEStack;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class EStorageCell<T extends IAEStack<T>> extends AEBaseItem implements IStorageCell<T> {
    protected final int totalBytes;

    public EStorageCell(final int miloBytes) {
        this.setMaxStackSize(1);
        this.totalBytes = miloBytes * 1024 * 1024;
    }

    @Override
    public int getBytes(@Nonnull final ItemStack cellItem) {
        return totalBytes;
    }

    @Override
    public boolean isBlackListed(@Nonnull final ItemStack cellItem, @Nonnull final T requestedAddition) {
        return false;
    }

    @Override
    public boolean storableInStorageCell() {
        return false;
    }

    @Override
    public boolean isStorageCell(@Nonnull final ItemStack i) {
        return true;
    }

    @Override
    public boolean isEditable(final ItemStack is) {
        return true;
    }

    @Override
    public IItemHandler getUpgradesInventory(final ItemStack is) {
        return new CellUpgrades(is, 2);
    }

    @Override
    public IItemHandler getConfigInventory(final ItemStack is) {
        return new CellConfig(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(final ItemStack is) {
        final String fz = Platform.openNbtData(is).getString("FuzzyMode");
        try {
            return FuzzyMode.valueOf(fz);
        } catch (final Throwable t) {
            return FuzzyMode.IGNORE_ALL;
        }
    }

    @Override
    public void setFuzzyMode(final ItemStack is, final FuzzyMode fzMode) {
        Platform.openNbtData(is).setString("FuzzyMode", fzMode.name());
    }
}
