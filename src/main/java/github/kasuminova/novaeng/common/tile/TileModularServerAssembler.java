package github.kasuminova.novaeng.common.tile;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileModularServerAssembler extends TileCustomController {
    protected IOInventory serverInventory;

    protected ModularServer server = new ModularServer(this, ItemStack.EMPTY);

    public TileModularServerAssembler() {
        this.serverInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(0);
        this.serverInventory.setStackLimit(1, 0);
        this.serverInventory.setListener(this::onServerInventoryUpdate);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "modular_server_assembler"));
        this.server.initInv();
        this.server.setOnServerInvChangedListener(this::onServerInternalInventoryUpdate);
    }

    @Override
    public void doControllerTick() {

    }

    public void onServerInventoryUpdate(final int changedSlot) {
        ItemStack stackInSlot = serverInventory.getStackInSlot(changedSlot);
//        if (stackInSlot.isEmpty()) {
//            server = null;
//            return;
//        }

        if (server == null || server.requiresUpdate(stackInSlot)) {
            server = new ModularServer(this, stackInSlot);
            if (stackInSlot.getTagCompound() != null) {
                server.readFullInvNBT(stackInSlot.getTagCompound().getCompoundTag("server"));
            } else {
                server.initInv();
            }
            server.setOnServerInvChangedListener(this::onServerInternalInventoryUpdate);
        }
    }

    public void onServerInternalInventoryUpdate(final ModularServer server) {
        ItemStack stackInSlot = serverInventory.getStackInSlot(0);
        if (stackInSlot.isEmpty() || server.requiresUpdate(stackInSlot)) {
            NovaEngineeringCore.log.warn("Server stack is not equals the cachedStack or it's empty! Assembler world: " + getWorld() + ", pos: " + MiscUtils.posToString(getPos()));
        }

        ItemStack copied = stackInSlot.copy();
        NBTTagCompound tag = copied.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            copied.setTagCompound(tag);
        }
        tag.setTag("server", server.writeNBT());

        server.setCachedStack(copied);
        serverInventory.setStackInSlot(0, copied);
    }

    public ModularServer getServer() {
        return server;
    }

    public TileModularServerAssembler setServer(final ModularServer server) {
        this.server = server;
        if (server != null) {
            server.setOnServerInvChangedListener(this::onServerInternalInventoryUpdate);
        }
        return this;
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("serverInv")) {
            serverInventory = IOInventory.deserialize(this, compound.getCompoundTag("serverInv"));
            serverInventory.setListener(this::onServerInventoryUpdate);
            onServerInventoryUpdate(-1);
        }
        if (compound.hasKey("controllerStatus")) {
            controllerStatus = CraftingStatus.deserialize(compound.getCompoundTag("controllerStatus"));
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setTag("serverInv", serverInventory.writeNBT());
        compound.setTag("controllerStatus", controllerStatus.serialize());
    }

    @Override
    public boolean isWorking() {
        return controllerStatus.isCrafting();
    }

}
