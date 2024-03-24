package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.AEApi;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngCellInventory;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageCellDrive;
import github.kasuminova.novaeng.common.estorage.ECellDriveWatcher;
import github.kasuminova.novaeng.common.estorage.EStorageCellHandler;
import github.kasuminova.novaeng.common.item.estorage.EStorageCell;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellFluid;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellItem;
import github.kasuminova.novaeng.common.network.PktCellDriveStatusUpdate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import static appeng.helpers.ItemStackHelper.stackFromNBT;
import static appeng.helpers.ItemStackHelper.stackWriteToNBT;

@SuppressWarnings("rawtypes")
public class EStorageCellDrive extends EStoragePart implements ISaveProvider, IAEAppEngInventory {

    protected final AppEngCellInventory driveInv = new AppEngCellInventory(this, 1);
    protected final Map<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler<?>> inventoryHandlers = new IdentityHashMap<>();

    protected EStorageCellHandler cellHandler = null;
    protected ECellDriveWatcher<IAEItemStack> watcher = null;

    protected boolean isCached = false;

    protected long lastWriteTick = 0;
    protected boolean writing = false;

    public EStorageCellDrive() {
    }

    public void updateWriteState() {
        long totalWorldTime = world.getTotalWorldTime();
        if (totalWorldTime - lastWriteTick >= 20 && writing) {
            writing = false;
            BlockPos pos = getPos();
            NovaEngineeringCore.NET_CHANNEL.sendToAllAround(
                    new PktCellDriveStatusUpdate(),
                    new NetworkRegistry.TargetPoint(
                            world.provider.getDimension(),
                            pos.getX(), pos.getY(), pos.getZ(),
                            32)
            );
        }
    }

    protected void updateHandler(final boolean refreshState) {
        if (isCached) {
            return;
        }
        watcher = null;
        cellHandler = null;
        inventoryHandlers.clear();
        isCached = true;
        ItemStack stack = driveInv.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }
        if ((cellHandler = EStorageCellHandler.getHandler(stack)) == null) {
            return;
        }
        ICellInventoryHandler cellInventory = null;
        final Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels = AEApi.instance().storage().storageChannels();
        for (final IStorageChannel<? extends IAEStack<?>> channel : storageChannels) {
            cellInventory = cellHandler.getCellInventory(stack, this, channel);
            if (cellInventory == null) {
                continue;
            }
            driveInv.setHandler(0, cellInventory);
            watcher = new ECellDriveWatcher<>(cellInventory, channel, this);
            watcher.setPriority(storageController.channel.getPriority());
            inventoryHandlers.put(channel, cellInventory);
            break;
        }

        if (cellInventory == null || !refreshState) {
            return;
        }
        updateDriveBlockState(stack, cellInventory);
    }

    protected void updateDriveBlockState(final ItemStack stack, final ICellInventoryHandler cellInventory) {
        IBlockState state = world.getBlockState(getPos());
        if (!(state.getBlock() instanceof BlockEStorageCellDrive)) {
            return;
        }

        EStorageCell<?> cell = (EStorageCell<?>) stack.getItem();
        BlockEStorageCellDrive.StorageLevel level = cell.getLevel();
        BlockEStorageCellDrive.StorageType type = getCellType(cell);
        if (type == null) {
            return;
        }

        world.setBlockState(getPos(), state
                .withProperty(BlockEStorageCellDrive.STORAGE_LEVEL, level)
                .withProperty(BlockEStorageCellDrive.STORAGE_TYPE, type)
                .withProperty(BlockEStorageCellDrive.STATUS, BlockEStorageCellDrive.Status.IDLE)
                .withProperty(BlockEStorageCellDrive.STORAGE_CAPACITY, getCapacity(cellInventory))
        );
    }

    public static BlockEStorageCellDrive.StorageType getCellType(final EStorageCell<?> cell) {
        BlockEStorageCellDrive.StorageType type;
        if (cell instanceof EStorageCellItem) {
            type = BlockEStorageCellDrive.StorageType.ITEM;
        } else if (cell instanceof EStorageCellFluid) {
            type = BlockEStorageCellDrive.StorageType.FLUID;
        } else {
            return null;
        }
        return type;
    }

    public static BlockEStorageCellDrive.StorageCapacity getCapacity(final ICellInventoryHandler cellInvHandler) {
        ICellInventory cellInv = cellInvHandler.getCellInv();
        if (cellInv == null) {
            return BlockEStorageCellDrive.StorageCapacity.EMPTY;
        }
        long totalTypes = cellInv.getTotalItemTypes();
        long storedTypes = cellInv.getStoredItemTypes();
        if (storedTypes == 0) {
            return BlockEStorageCellDrive.StorageCapacity.EMPTY;
        }
        if (cellInv.getFreeBytes() <= 0) {
            return BlockEStorageCellDrive.StorageCapacity.FULL;
        }
        if (storedTypes >= totalTypes) {
            return BlockEStorageCellDrive.StorageCapacity.TYPE_MAX;
        }
        return BlockEStorageCellDrive.StorageCapacity.EMPTY;
    }

    @SuppressWarnings("unchecked")
    public <T extends IAEStack<T>> IMEInventoryHandler<T> getHandler(final IStorageChannel<T> channel) {
        updateHandler(false);
        IMEInventoryHandler<?> handler = inventoryHandlers.get(channel);
        return handler == null ? null : (IMEInventoryHandler<T>) handler;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.isCached) {
            this.isCached = false; // recalculate the storage cell.
            this.updateHandler(true);
        }

        EStorageMEChannel channel = getController().getChannel();
        if (channel == null) {
            this.markNoUpdateSync();
            return;
        }

        AENetworkProxy proxy = channel.getProxy();
        IActionSource source = channel.getSource();

        try {
            if (proxy.isActive()) {
                final IStorageGrid gs = proxy.getStorage();
                postChanges(gs, removed, added, source);
            }
            proxy.getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException ignored) {
        }

        this.markNoUpdateSync();
    }

    @Override
    public void onDisassembled() {
        super.onDisassembled();
        EStorageMEChannel channel = getController().getChannel();
        if (channel == null) {
            this.markNoUpdateSync();
            return;
        }

        AENetworkProxy proxy = channel.getProxy();
        IActionSource source = channel.getSource();

        try {
            if (proxy.isActive()) {
                ItemStack removed = driveInv.getStackInSlot(0);
                final IStorageGrid gs = proxy.getStorage();
                postChanges(gs, removed, ItemStack.EMPTY, source);
            }
            proxy.getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public void postChanges(final IStorageGrid gs, final ItemStack removed, final ItemStack added, final IActionSource src) {
        for (final IStorageChannel<?> chan : AEApi.instance().storage().storageChannels()) {
            final IItemList<?> myChanges = chan.createList();

            if (!removed.isEmpty()) {
                final IMEInventory myInv = cellHandler.getCellInventory(removed, null, chan);
                if (myInv != null) {
                    myInv.getAvailableItems(myChanges);
                    for (final IAEStack is : myChanges) {
                        is.setStackSize(-is.getStackSize());
                    }
                }
            }
            if (!added.isEmpty()) {
                final IMEInventory myInv = cellHandler.getCellInventory(added, null, chan);
                if (myInv != null) {
                    myInv.getAvailableItems(myChanges);
                }
            }
            gs.postAlterationOfStoredItems(chan, myChanges, src);
        }
    }

    public AppEngCellInventory getDriveInv() {
        return driveInv;
    }

    public void onWriting() {
        this.writing = true;
        this.lastWriteTick = world.getTotalWorldTime();
    }

    public boolean isWriting() {
        return writing;
    }

    @Nullable
    @Override
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) driveInv;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound tag) {
        final NBTTagCompound opt = tag.getCompoundTag("driveInv");
        for (int x = 0; x < driveInv.getSlots(); x++) {
            final NBTTagCompound item = opt.getCompoundTag("item" + x);
            ItemHandlerUtil.setStackInSlot(driveInv, x, stackFromNBT(item));
        }

        super.readCustomNBT(tag);
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound tag) {
        final NBTTagCompound opt = new NBTTagCompound();
        for (int x = 0; x < driveInv.getSlots(); x++) {
            final NBTTagCompound itemNBT = new NBTTagCompound();
            final ItemStack is = driveInv.getStackInSlot(x);
            if (!is.isEmpty()) {
                stackWriteToNBT(is, itemNBT);
            }
            opt.setTag("item" + x, itemNBT);
        }
        tag.setTag("driveInv", opt);

        super.writeCustomNBT(tag);
    }

    @Override
    public void saveChanges(@Nullable final ICellInventory<?> cellInventory) {
        saveChanges();
    }

    @Override
    public void saveChanges() {
        markDirty();
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            this.world.markChunkDirty(this.pos, this);
        }
    }
}
