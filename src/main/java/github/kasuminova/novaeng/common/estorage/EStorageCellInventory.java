package github.kasuminova.novaeng.common.estorage;

import appeng.api.config.Actionable;
import appeng.api.exceptions.AppEngException;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.me.storage.AbstractCellInventory;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EStorageCellInventory<T extends IAEStack<T>> extends AbstractCellInventory<T> {
    private final IStorageChannel<T> channel;

    protected EStorageCellInventory(final IStorageCell<T> cellType, final ItemStack o, final ISaveProvider container) {
        super(cellType, o, container);
        this.channel = cellType.getChannel();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends IAEStack<T>> ICellInventory<T> createInventory(final ItemStack o, final ISaveProvider container) {
        try {
            if (o == null) {
                throw new AppEngException("ItemStack was used as a cell, but was not a cell!");
            }

            final Item type = o.getItem();
            if (!(type instanceof IStorageCell cellType)) {
                throw new AppEngException("ItemStack was used as a cell, but was not a cell!");
            }

            if (!cellType.isStorageCell(o)) {
                throw new AppEngException("ItemStack was used as a cell, but was not a cell!");
            }

            return new EStorageCellInventory<T>(cellType, o, container);
        } catch (final AppEngException e) {
            NovaEngineeringCore.log.error(e);
            return null;
        }
    }

    private boolean isStorageCell(final T input) {
        if (input instanceof final IAEItemStack stack) {
            final IStorageCell<?> type = getStorageCell(stack.getDefinition());

            return type != null && !type.storableInStorageCell();
        }

        return false;
    }

    private static IStorageCell<?> getStorageCell(final ItemStack input) {
        if (input != null) {
            final Item type = input.getItem();

            if (type instanceof IStorageCell) {
                return (IStorageCell<?>) type;
            }
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean isCellEmpty(ICellInventory inv) {
        if (inv != null) {
            return inv.getAvailableItems(inv.getChannel().createList()).isEmpty();
        }
        return true;
    }

    @Override
    public T injectItems(T input, Actionable mode, IActionSource src) {
        if (input == null) {
            return null;
        }
        if (input.getStackSize() == 0) {
            return null;
        }

        if (this.cellType.isBlackListed(this.getItemStack(), input)) {
            return input;
        }
        // This is slightly hacky as it expects a read-only access, but fine for now.
        // TODO: Guarantee a read-only access. E.g. provide an isEmpty() method and ensure CellInventory does not write
        // any NBT data for empty cells instead of relying on an empty IItemContainer
        if (isStorageCell(input)) {
            final ICellInventory<?> meInventory = createInventory(((IAEItemStack) input).createItemStack(), null);
            if (!isCellEmpty(meInventory)) {
                return input;
            }
        }

        final T l = this.getCellItems().findPrecise(input);
        if (l != null) {
            final long remainingItemCount = this.getRemainingItemCount();
            if (remainingItemCount <= 0) {
                return input;
            }

            if (input.getStackSize() > remainingItemCount) {
                final T r = input.copy();
                r.setStackSize(r.getStackSize() - remainingItemCount);
                if (mode == Actionable.MODULATE) {
                    l.setStackSize(l.getStackSize() + remainingItemCount);
                    this.saveChanges();
                }
                return r;
            } else {
                if (mode == Actionable.MODULATE) {
                    l.setStackSize(l.getStackSize() + input.getStackSize());
                    this.saveChanges();
                }
                return null;
            }
        }

        // room for new type, and for at least one item!
        if (this.canHoldNewItem()) {
            final long remainingItemCount = this.getRemainingItemCount() - (long) this.getBytesPerType() * this.itemsPerByte;
            if (remainingItemCount > 0) {
                if (input.getStackSize() > remainingItemCount) {
                    final T toReturn = input.copy();
                    toReturn.setStackSize(input.getStackSize() - remainingItemCount);
                    if (mode == Actionable.MODULATE) {
                        final T toWrite = input.copy();
                        toWrite.setStackSize(remainingItemCount);

                        this.cellItems.add(toWrite);
                        this.saveChanges();
                    }
                    return toReturn;
                }

                if (mode == Actionable.MODULATE) {
                    this.cellItems.add(input);
                    this.saveChanges();
                }

                return null;
            }
        }

        return input;
    }

    @Override
    public T extractItems(T request, Actionable mode, IActionSource src) {
        if (request == null) {
            return null;
        }

        final long size = Math.min(Integer.MAX_VALUE, request.getStackSize());

        T Results = null;

        final T l = this.getCellItems().findPrecise(request);
        if (l != null) {
            Results = l.copy();

            if (l.getStackSize() <= size) {
                Results.setStackSize(l.getStackSize());
                if (mode == Actionable.MODULATE) {
                    l.setStackSize(0);
                    this.saveChanges();
                }
            } else {
                Results.setStackSize(size);
                if (mode == Actionable.MODULATE) {
                    l.setStackSize(l.getStackSize() - size);
                    this.saveChanges();
                }
            }
        }

        return Results;
    }

    @Override
    public IStorageChannel<T> getChannel() {
        return this.channel;
    }

    @Override
    protected boolean loadCellItem(NBTTagCompound compoundTag, long stackSize) {
        // Now load the item stack
        final T t;
        try {
            t = this.channel.createFromNBT(compoundTag);
            if (t == null) {
                AELog.warn("Removing item " + compoundTag + " from storage cell because the associated item type couldn't be found.");
                return false;
            }
        } catch (Throwable ex) {
            if (AEConfig.instance().isRemoveCrashingItemsOnLoad()) {
                AELog.warn(ex, "Removing item " + compoundTag + " from storage cell because loading the ItemStack crashed.");
                return false;
            }
            throw ex;
        }

        t.setStackSize(stackSize);
        t.setCraftable(false);

        if (stackSize > 0) {
            this.cellItems.add(t);
        }

        return true;
    }

    @Override
    public long getUsedBytes() {
        final long bytesForItemCount = (this.getStoredItemCount() + this.getUnusedItemCount()) / (this.itemsPerByte * 8L);
        return this.getStoredItemTypes() * this.getBytesPerType() + bytesForItemCount;
    }

    @Override
    public long getRemainingItemCount() {
        final long remaining = this.getFreeBytes() * (this.itemsPerByte * 8L) + this.getUnusedItemCount();
        return remaining > 0 ? remaining : 0;
    }

    @Override
    public int getUnusedItemCount() {
        final int div = (int) (this.getStoredItemCount() % 8);

        if (div == 0) {
            return 0;
        }

        return (this.itemsPerByte * 8) - div;
    }

}
