package github.kasuminova.novaeng.common.item.estorage;

import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.helper.FluidCellConfig;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class EStorageCellFluid extends EStorageCell<IAEFluidStack> {

    public static final EStorageCellFluid LEVEL_A = new EStorageCellFluid(DriveStorageLevel.A, 16, 8);
    public static final EStorageCellFluid LEVEL_B = new EStorageCellFluid(DriveStorageLevel.B, 64, 64);
    public static final EStorageCellFluid LEVEL_C = new EStorageCellFluid(DriveStorageLevel.C, 256, 512);

    public EStorageCellFluid(final DriveStorageLevel level, final int millionBytes, final int byteMultiplier) {
        super(level, millionBytes, byteMultiplier);
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_cell_fluid_" + millionBytes + "m"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_cell_fluid_" + millionBytes + "m");
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

    @Override
    public IItemHandler getConfigInventory(final ItemStack is) {
        return new FluidCellConfig(is);
    }

    @Nonnull
    @Override
    public IStorageChannel<IAEFluidStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
    }
}
