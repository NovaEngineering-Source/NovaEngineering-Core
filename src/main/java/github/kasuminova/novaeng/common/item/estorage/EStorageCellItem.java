package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.estorage.prop.DriveStorageLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EStorageCellItem extends EStorageCell<IAEItemStack> {

    public static final EStorageCellItem LEVEL_A = new EStorageCellItem(DriveStorageLevel.A, 16, 4);
    public static final EStorageCellItem LEVEL_B = new EStorageCellItem(DriveStorageLevel.B, 64, 16);
    public static final EStorageCellItem LEVEL_C = new EStorageCellItem(DriveStorageLevel.C, 256, 64);

    public EStorageCellItem(final DriveStorageLevel level, final int millionBytes, final int byteMultiplier) {
        super(level, millionBytes, byteMultiplier);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_cell_item_" + millionBytes + "m"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_cell_item_" + millionBytes + "m");
    }

    @Override
    public int getTotalTypes(@Nonnull final ItemStack cellItem) {
        return 315;
    }

    @Override
    public int getBytesPerType(@Nonnull final ItemStack cellItem) {
        return byteMultiplier * 1024;
    }

    @Nonnull
    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}
