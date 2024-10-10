package github.kasuminova.novaeng.common.item.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.DriveStorageLevel;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ECalculatorCell extends Item {

    public static final ECalculatorCell L4 = new ECalculatorCell(DriveStorageLevel.A, 64);
    public static final ECalculatorCell L6 = new ECalculatorCell(DriveStorageLevel.B, 1024);
    public static final ECalculatorCell L9 = new ECalculatorCell(DriveStorageLevel.C, 16384);

    protected final DriveStorageLevel level;
    protected final long totalBytes;

    public ECalculatorCell(DriveStorageLevel level, final long millionBytes) {
        this.level = level;
        this.totalBytes = (millionBytes * 1000) * 1024;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabNovaEng.INSTANCE);
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "ecalculator_cell_" + millionBytes + "m"));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "ecalculator_cell_" + millionBytes + "m");
    }

    public DriveStorageLevel getLevel() {
        return level;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

}
