package github.kasuminova.novaeng.common.container;

import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerHyperNetTerminal extends ContainerBase<TileHyperNetTerminal> {
    private final Slot slotCard;

    public ContainerHyperNetTerminal(final TileHyperNetTerminal owner, final EntityPlayer opening) {
        super(owner, opening);

        this.slotCard = addSlotToContainer(new SlotCard(
                owner.getCardInventory().asGUIAccess(),
                TileHyperNetTerminal.NETWORK_CONNECT_CARD_SLOT, 159, 226));
    }

    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();

            if (index < 36) {
                if (!slotStack.isEmpty()) {
                    Slot sb = this.inventorySlots.get(this.slotCard.slotNumber);
                    if (!sb.getHasStack()) {
                        if (!this.mergeItemStack(slotStack, sb.slotNumber, sb.slotNumber + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (index < 27) {
                if (!this.mergeItemStack(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 36) {
                if (!this.mergeItemStack(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemstack;
    }

    @Override
    protected void addPlayerSlots(EntityPlayer opening) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(opening.inventory, j + i * 9 + 9, 182 + j * 18, 168 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(opening.inventory, i, 182 + i * 18, 226));
        }
    }

    public static class SlotCard extends SlotItemHandler {

        public SlotCard(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            if (stack.getItem() != RegistryHyperNet.getHyperNetConnectCard()) {
                return false;
            }
            return super.isItemValid(stack);
        }
    }
}
