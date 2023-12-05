package github.kasuminova.novaeng.common.tile;

import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.util.ResourceLocation;

public class TileModularServerHosts extends TileCustomController {
    protected IOInventory serverInventory;

    public TileModularServerHosts() {
        this.serverInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(0, 1, 2);
        this.serverInventory.setStackLimit(1, 0);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "modular_server_hosts"));
    }

    @Override
    public void doControllerTick() {

    }

    @Override
    public boolean isWorking() {
        return controllerStatus.isCrafting();
    }

}
