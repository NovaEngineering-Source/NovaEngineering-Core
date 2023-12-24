package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.item.ItemModularServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotModularServer extends SlotItemHandler {

    public SlotModularServer(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return stack.getItem() instanceof ItemModularServer;
    }

    @Override
    public boolean canTakeStack(final EntityPlayer playerIn) {
        return super.canTakeStack(playerIn);
    }

    @Override
    public void putStack(@Nonnull final ItemStack stack) {
        super.putStack(stack);
    }

    @Nonnull
    @Override
    public ItemStack onTake(@Nonnull final EntityPlayer thePlayer, @Nonnull final ItemStack stack) {
        return super.onTake(thePlayer, stack);
    }
}
