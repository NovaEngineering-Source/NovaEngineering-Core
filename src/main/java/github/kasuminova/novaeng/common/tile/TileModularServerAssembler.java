package github.kasuminova.novaeng.common.tile;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ServerInvProvider;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileModularServerAssembler extends TileCustomController implements ServerInvProvider {
    protected IOInventory serverInventory;

    protected ModularServer server = new ModularServer(this, ItemStack.EMPTY);

    public TileModularServerAssembler() {
        this.serverInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(0);
        this.serverInventory.setStackLimit(1, 0);
        this.serverInventory.setListener(this::onServerInventoryUpdate);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "modular_server_assembler"));
        this.server.initInv();
    }

    @Override
    public void doControllerTick() {

    }

    public void onServerInventoryUpdate() {
        ItemStack stackInSlot = serverInventory.getStackInSlot(0);
        if (server == null || server.requiresUpdate(stackInSlot)) {
            server = new ModularServer(this, stackInSlot);
            if (stackInSlot.getTagCompound() != null) {
                server.readFullInvNBT(stackInSlot.getTagCompound());
            } else {
                server.initInv();
            }
        }
    }

    public ModularServer getServer() {
        return server;
    }

    public TileModularServerAssembler setServer(final ModularServer server) {
        this.server = server;
        return this;
    }

    @Override
    public ServerModuleInv getInvByName(final String name) {
        return server.getInvByName(name);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("serverInv")) {
            serverInventory = IOInventory.deserialize(this, compound.getCompoundTag("serverInv"));
            serverInventory.setListener(this::onServerInventoryUpdate);
            onServerInventoryUpdate();
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
