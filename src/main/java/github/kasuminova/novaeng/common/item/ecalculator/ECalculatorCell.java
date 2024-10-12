package github.kasuminova.novaeng.common.item.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.DriveStorageLevel;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

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

    @Override
    public void addInformation(@Nonnull final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip, @Nonnull final ITooltipFlag flagIn) {
        tooltip.add(I18n.format("novaeng.ecalculator_cell.insert.tip"));
        tooltip.add(I18n.format("novaeng.ecalculator_cell.extract.tip"));
        tooltip.add(I18n.format("novaeng.ecalculator_cell.tip.0"));
        tooltip.add(I18n.format("novaeng.ecalculator_cell.tip.1"));
        tooltip.add(I18n.format("novaeng.ecalculator_cell.tip.2"));
        final ECalculatorCell cell = (ECalculatorCell) stack.getItem();
        if (cell == L6) {
            tooltip.add(I18n.format("novaeng.ecalculator_cell.l6.tip"));
        } else if (cell == L9) {
            tooltip.add(I18n.format("novaeng.ecalculator_cell.l9.tip"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public DriveStorageLevel getLevel() {
        return level;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

}
