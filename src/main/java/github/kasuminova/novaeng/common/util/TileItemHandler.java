package github.kasuminova.novaeng.common.util;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import hellfirepvp.modularmachinery.common.util.IItemHandlerImpl;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class TileItemHandler extends IItemHandlerImpl {
    protected final String invName;
    protected final BitSet availableSlots = new BitSet();
    protected final TileEntitySynchronized owner;

    private IntConsumer onChangedListener = null;

    public static TileItemHandler create(final TileEntitySynchronized owner, final int slotCount, final String invName) {
        int[] slotIDs = new int[slotCount];
        for (int slotID = 0; slotID < slotIDs.length; slotID++) {
            slotIDs[slotID] = slotID;
        }
        return new TileItemHandler(owner, slotIDs, slotIDs, invName).setAllSlotAvailable().updateSlotLimits();
    }

    public TileItemHandler(final TileEntitySynchronized owner, final int[] inSlots, final int[] outSlots, final String invName) {
        super(inSlots, outSlots);
        this.owner = owner;
        this.invName = invName;
    }

    public TileItemHandler updateInOutSlots() {
        int[] slotIDs = new int[inventory.length];
        for (int slotID = 0; slotID < slotIDs.length; slotID++) {
            slotIDs[slotID] = slotID;
        }
        this.inSlots = slotIDs;
        this.outSlots = slotIDs;
        return this;
    }

    public TileItemHandler updateSlotLimits() {
        int[] slotLimits = new int[inventory.length];
        Arrays.fill(slotLimits, 1);
        this.slotLimits = slotLimits;
        return this;
    }

    public TileItemHandler setOnChangedListener(final IntConsumer onChangedListener) {
        this.onChangedListener = onChangedListener;
        return this;
    }

    public boolean isSlotAvailable(final int slotID) {
        return availableSlots.get(slotID);
    }

    public TileItemHandler setSlotAvailable(final int slotID) {
        availableSlots.set(slotID);
        return this;
    }

    public TileItemHandler setAllSlotAvailable() {
        availableSlots.set(0, inventory.length);
        return this;
    }

    public TileItemHandler setUnavailableSlots(final int[] slotIDs) {
        for (final int slotID : slotIDs) {
            availableSlots.set(slotID, false);
        }
        return this;
    }

    public TileItemHandler setUnavailableSlot(final int slotID) {
        availableSlots.set(slotID, false);
        return this;
    }

    public IntStream getAvailableSlotsStream() {
        return availableSlots.stream();
    }

    public String getInvName() {
        return invName;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        super.setStackInSlot(slot, stack);
        if (this.onChangedListener != null) {
            this.onChangedListener.accept(slot);
        }
        if (this.owner != null) {
            this.owner.markNoUpdateSync();
        }
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }
        ItemStack inserted = this.insertItemInternal(slot, stack, simulate);
        if (!simulate) {
            if (this.onChangedListener != null) {
                this.onChangedListener.accept(slot);
            }
            if (this.owner != null) {
                this.owner.markNoUpdateSync();
            }
        }
        return inserted;
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted = super.extractItem(slot, amount, simulate);
        if (!simulate) {
            if (this.onChangedListener != null) {
                this.onChangedListener.accept(slot);
            }
            if (this.owner != null) {
                this.owner.markNoUpdateSync();
            }
        }
        return extracted;
    }

    public NBTTagCompound writeNBT() {
        List<ItemStack> stackSet = new ArrayList<>();
        int[] stackSetIdxSet = new int[inventory.length];

        invSet:
        for (int i = 0; i < inventory.length; i++) {
            SlotStackHolder holder = this.inventory[i];
            ItemStack stackInHolder = holder.itemStack;
            if (stackInHolder.isEmpty()) {
                stackSetIdxSet[i] = -1;
                continue;
            }

            for (int stackSetIdx = 0; stackSetIdx < stackSet.size(); stackSetIdx++) {
                ItemStack stackInSet = stackSet.get(stackSetIdx);
                if (ItemUtils.matchStacks(stackInHolder, stackInSet)) {
                    stackSetIdxSet[i] = stackSetIdx;
                    continue invSet;
                }
            }

            stackSet.add(stackInHolder);
            stackSetIdxSet[i] = stackSet.size() - 1;
        }

        NBTTagList stackSetTag = new NBTTagList();
        NBTTagList invSetTag = new NBTTagList();

        for (final ItemStack stack : stackSet) {
            NBTTagCompound stackTag = stack.writeToNBT(new NBTTagCompound());
            if (stack.getCount() >= 127) {
                stackTag.setInteger("Count", stack.getCount());
            }
            stackSetTag.appendTag(stackTag);
        }
        for (final int setIdx : stackSetIdxSet) {
            invSetTag.appendTag(new NBTTagByte((byte) setIdx));
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("stackSet", stackSetTag);
        tag.setTag("invSet", invSetTag);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        NBTTagList stackSetTag = tag.getTagList("stackSet", Constants.NBT.TAG_COMPOUND);
        NBTTagList invSetTag = tag.getTagList("invSet", Constants.NBT.TAG_BYTE);

        List<ItemStack> stackSet = new ArrayList<>();
        for (int i = 0; i < stackSetTag.tagCount(); i++) {
            NBTTagCompound stackTag = stackSetTag.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(stackTag);
            stack.setCount(stackTag.getInteger("Count"));
            stackSet.add(stack);
        }

        this.inventory = new SlotStackHolder[invSetTag.tagCount()];
        for (int i = 0; i < invSetTag.tagCount(); i++) {
            SlotStackHolder holder = new SlotStackHolder(i);
            int setIdx = ((NBTPrimitive) invSetTag.get(i)).getByte();
            if (setIdx != -1) {
                holder.itemStack = stackSet.get(setIdx).copy();
            }
            this.inventory[i] = holder;
        }

        updateInOutSlots();
        updateSlotLimits();

        if (onChangedListener != null) {
            onChangedListener.accept(-1);
        }
    }
}
