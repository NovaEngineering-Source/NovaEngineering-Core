package github.kasuminova.novaeng.common.tile;

import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import hellfirepvp.modularmachinery.common.tiles.base.TileColorableMachineComponent;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TileModularServerFrame extends TileColorableMachineComponent {
    protected final ModularServer[] servers = new ModularServer[3];
    protected IOInventory serverInventory;
    protected TileMultiblockMachineController parent = null;

    public TileModularServerFrame() {
        this.serverInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(0, 1, 2);
        this.serverInventory.setStackLimit(1, 0, 1, 2);
        this.serverInventory.setListener(this::onServerInventoryUpdate);
    }

    public ModularServer[] getServers() {
        return Arrays.copyOf(servers, 3);
    }

    //

    // Inventory Listener

    protected void onServerInventoryUpdate(final int changedSlot) {
        ItemStack stackInSlot = serverInventory.getStackInSlot(changedSlot);
        ModularServer server = servers[changedSlot];

        if (stackInSlot.isEmpty()) {
            if (server != null) {
                server.invalidate();
            }
            servers[changedSlot] = null;
            return;
        }

        if (server == null || server.requiresUpdate(stackInSlot)) {
            if (server != null) {
                server.invalidate();
            }

            server = new ModularServer(this, stackInSlot);
            if (stackInSlot.getTagCompound() != null) {
                server.readFullInvNBT(stackInSlot.getTagCompound().getCompoundTag("server"));
            } else {
                server.initInv();
            }

            server.initModules();
        }

        servers[changedSlot] = server;
    }

    // NBT

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);
        for (int i = 0; i < servers.length; i++) {
            ModularServer server = servers[i];
            if (server != null) {
                server.invalidate();
            }
            servers[i] = null;
        }

        if (compound.hasKey("serverInv")) {
            serverInventory = IOInventory.deserialize(this, compound.getCompoundTag("serverInv"));
            serverInventory.setListener(this::onServerInventoryUpdate);
            for (int i = 0; i < 3; i++) {
                onServerInventoryUpdate(i);
            }
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setTag("serverInv", serverInventory.writeNBT());
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(serverInventory);
        }
        return super.getCapability(capability, facing);
    }

}
