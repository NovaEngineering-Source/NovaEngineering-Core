package github.kasuminova.novaeng.common.container;

import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerModularServerAssembler extends ContainerBase<TileModularServerAssembler> {
    protected final AssemblySlotManager slotManager;

    public ContainerModularServerAssembler(final TileModularServerAssembler owner, final EntityPlayer opening) {
        super(owner, opening);
        this.slotManager = owner.getServer().getSlotManager();
        this.slotManager.addAllSlotToContainer(this);
    }

    @Nonnull
    @Override
    public Slot addSlotToContainer(@Nonnull final Slot slotIn) {
        return super.addSlotToContainer(slotIn);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull final EntityPlayer playerIn, final int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index < 36) {
//                if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ItemBlueprint) {
//                    Slot sb = this.inventorySlots.get(this.slotBlueprint.slotNumber);
//                    if (!sb.getHasStack()) {
//                        if (!this.mergeItemStack(itemstack1, sb.slotNumber, sb.slotNumber + 1, false)) {
//                            return ItemStack.EMPTY;
//                        }
//                    }
//                }
            }

            if (index < 27) {
                if (!this.mergeItemStack(stackInSlot, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 36) {
                if (!this.mergeItemStack(stackInSlot, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackInSlot);
        }

        return itemstack;
    }

    public AssemblySlotManager getSlotManager() {
        return slotManager;
    }

    @Override
    protected void addPlayerSlots(EntityPlayer opening) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(opening.inventory, j + i * 9 + 9, 133 + j * 18, 124 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(opening.inventory, i, 133 + i * 18, 182));
        }
    }
}
