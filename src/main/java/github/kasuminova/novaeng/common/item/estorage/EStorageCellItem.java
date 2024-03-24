package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EStorageCellItem extends EStorageCell<IAEItemStack> {

    public static final EStorageCellItem LEVEL_A = new EStorageCellItem(DriveStorageLevel.A, 8,  8);
    public static final EStorageCellItem LEVEL_B = new EStorageCellItem(DriveStorageLevel.B, 32, 64);
    public static final EStorageCellItem LEVEL_C = new EStorageCellItem(DriveStorageLevel.C, 128, 512);

    public EStorageCellItem(final DriveStorageLevel level, final int millionBytes, final int byteMultiplier) {
        super(level, millionBytes, byteMultiplier);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_cell_item_" + millionBytes + "m"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_cell_item_" + millionBytes + "m");
    }

    @Override
    public int getTotalTypes(@Nonnull final ItemStack cellItem) {
        return 27;
    }

    @Override
    public int getBytesPerType(@Nonnull final ItemStack cellItem) {
        return 1024;
    }

    @Override
    public double getIdleDrain() {
        return 0;
    }

    @Nonnull
    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}
