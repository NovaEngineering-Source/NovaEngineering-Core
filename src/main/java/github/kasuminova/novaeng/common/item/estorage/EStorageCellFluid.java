package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageCellDrive;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EStorageCellFluid extends EStorageCell<IAEFluidStack> {

    public EStorageCellFluid(final BlockEStorageCellDrive.StorageLevel level, final int miloBytes, final int byteMultiplier) {
        super(level, miloBytes, byteMultiplier);
    }

    @Override
    public int getTotalTypes(@Nonnull final ItemStack cellItem) {
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
