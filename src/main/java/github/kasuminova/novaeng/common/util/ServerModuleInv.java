package github.kasuminova.novaeng.common.util;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import hellfirepvp.modularmachinery.common.util.IItemHandlerImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.BitSet;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ServerModuleInv extends IItemHandlerImpl {
    protected final String invName;
    protected final BitSet availableSlots = new BitSet();
    protected final TileEntitySynchronized owner;

    private Consumer<ServerModuleInv> onChangedListener = null;

    public static ServerModuleInv create(final TileEntitySynchronized owner, final int slotCount, final String invName) {
        int[] slotIDs = new int[slotCount];
        for (int slotID = 0; slotID < slotIDs.length; slotID++) {
            slotIDs[slotID] = slotID;
        }
        return new ServerModuleInv(owner, slotIDs, slotIDs, invName).setAllSlotAvailable();
    }

    public ServerModuleInv(final TileEntitySynchronized owner, final int[] inSlots, final int[] outSlots, final String invName) {
        super(inSlots, outSlots);
        this.owner = owner;
        this.invName = invName;
    }

    public ServerModuleInv updateInOutSlots() {
        int[] slotIDs = new int[inventory.length];
        for (int slotID = 0; slotID < slotIDs.length; slotID++) {
            slotIDs[slotID] = slotID;
        }
        this.inSlots = slotIDs;
        this.outSlots = slotIDs;
        return this;
    }

    public ServerModuleInv updateSlotLimits() {
        int[] slotLimits = new int[inventory.length];
        Arrays.fill(slotLimits, 1);
        this.slotLimits = slotLimits;
        return this;
    }

    public ServerModuleInv setOnChangedListener(final Consumer<ServerModuleInv> onChangedListener) {
        this.onChangedListener = onChangedListener;
        return this;
    }

    public boolean isSlotAvailable(final int slotID) {
        return availableSlots.get(slotID);
    }

    public ServerModuleInv setSlotAvailable(final int slotID) {
        availableSlots.set(slotID);
        return this;
    }

    public ServerModuleInv setAllSlotAvailable() {
        availableSlots.set(0, inventory.length);
        return this;
    }

    public ServerModuleInv setUnavailableSlots(final int[] slotIDs) {
        for (final int slotID : slotIDs) {
            availableSlots.set(slotID, false);
        }
        return this;
    }

    public ServerModuleInv setUnavailableSlot(final int slotID) {
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
            this.onChangedListener.accept(this);
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
                this.onChangedListener.accept(this);
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
                this.onChangedListener.accept(this);
            }
            if (this.owner != null) {
                this.owner.markNoUpdateSync();
            }
        }
        return extracted;
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList inv = new NBTTagList();
        for (int slot = 0; slot < inventory.length; slot++) {
            SlotStackHolder holder = this.inventory[slot];
            NBTTagCompound holderTag = new NBTTagCompound();
            ItemStack stack = holder.itemStack;

            holderTag.setInteger("id", slot);
            if (stack.isEmpty()) {
                holderTag.setBoolean("empty", true);
            } else {
                stack.writeToNBT(holderTag);
                if (stack.getCount() >= 127) {
                    holderTag.setInteger("Count", stack.getCount());
                }
            }

            inv.appendTag(holderTag);
        }
        tag.setTag("inv", inv);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("inv", Constants.NBT.TAG_COMPOUND);

        int tagCount = list.tagCount();
        this.inventory = new SlotStackHolder[tagCount];
        for (int i = 0; i < tagCount; i++) {
            NBTTagCompound holderTag = list.getCompoundTagAt(i);
            int slot = holderTag.getInteger("id");
            checkInventoryLength(slot);

            ItemStack stack = ItemStack.EMPTY;
            if (!holderTag.hasKey("empty")) {
                stack = new ItemStack(holderTag);
                stack.setCount(holderTag.getInteger("Count"));
            }

            SlotStackHolder holder = new SlotStackHolder(slot);
            holder.itemStack = stack;
            this.inventory[slot] = holder;
        }

        updateInOutSlots();
        updateSlotLimits();

        if (onChangedListener != null) {
            onChangedListener.accept(this);
        }
    }
}
