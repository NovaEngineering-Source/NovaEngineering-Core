package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EFluidStorageCell extends EStorageCell<IAEFluidStack> {
    public EFluidStorageCell(final int miloBytes) {
        super(miloBytes);
    }

    @Override
    public int getTotalTypes(final ItemStack cellItem) {
        return 3;
    }

    @Override
    public int getBytesPerType(@Nonnull final ItemStack cellItem) {
        return 3072;
    }

    @Override
    public double getIdleDrain() {
        return 0;
    }

    @Nonnull
    @Override
    public IStorageChannel<IAEFluidStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
    }
}
