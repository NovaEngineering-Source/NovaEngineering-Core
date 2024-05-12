package github.kasuminova.novaeng.common.tile;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class TileModularServerAssembler extends TileCustomController {
    protected IOInventory serverInventory;

    protected ModularServer server = null;

    protected Set<ContainerModularServerAssembler> openedContainer = new HashSet<>();

    public TileModularServerAssembler() {
        this.serverInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(0);
        this.serverInventory.setStackLimit(1, 0);
        this.serverInventory.setListener(this::onServerInventoryUpdate);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "modular_server_assembler"));
//        this.server.initInv();
//        this.server.setOnServerInvChangedListener(this::onServerInternalInventoryUpdate);
    }

    @Override
    public void doControllerTick() {

    }

    protected void onServerInventoryUpdate(final int changedSlot) {
        ItemStack stackInSlot = serverInventory.getStackInSlot(changedSlot);
        if (stackInSlot.isEmpty()) {
            server = null;
            openedContainer.forEach(ContainerModularServerAssembler::reInitSlots);
            notifyClientGUIInventoryUpdate();
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

            server.setOnServerInvChangedListener(this::onServerInternalInventoryUpdate);
            openedContainer.forEach(ContainerModularServerAssembler::reInitSlots);
        }
        if (server != null) {
            server.initModules();
        }

        notifyClientGUIInventoryUpdate();
    }

    protected void notifyClientGUIInventoryUpdate() {
        if (world != null && world.isRemote) {
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof GuiModularServerAssembler assemblerGUI && assemblerGUI.getAssembler() == this) {
                assemblerGUI.onServerInventoryUpdate();
            }
        }
    }

    protected void onServerInternalInventoryUpdate(final ModularServer server) {
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

    // Inventories

    public IOInventory getServerInventory() {
        return serverInventory;
    }

    // Containers

    public void addContainer(final ContainerModularServerAssembler container) {
        openedContainer.add(container);
    }

    public void removeContainer(final ContainerModularServerAssembler container) {
        openedContainer.remove(container);
    }

    // NBT

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("serverInv")) {
            serverInventory = IOInventory.deserialize(this, compound.getCompoundTag("serverInv"));
            serverInventory.setListener(this::onServerInventoryUpdate);
            onServerInventoryUpdate(0);
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setTag("serverInv", serverInventory.writeNBT());
    }

    @Override
    public void invalidate() {
        serverInventory.clear();
        if (server != null) {
            server.invalidate();
        }

        super.invalidate();
    }

    @Override
    public boolean isWorking() {
        return controllerStatus.isCrafting();
    }

}
