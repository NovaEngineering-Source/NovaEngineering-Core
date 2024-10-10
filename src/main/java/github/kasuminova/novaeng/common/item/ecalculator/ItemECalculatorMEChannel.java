package github.kasuminova.novaeng.common.item.ecalculator;

import github.kasuminova.novaeng.common.item.ItemBlockME;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemECalculatorMEChannel extends ItemBlockME {

    public ItemECalculatorMEChannel(final Block block) {
        super(block);
    }

    @Override
    public void addInformation(@Nonnull final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip, @Nonnull final ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
