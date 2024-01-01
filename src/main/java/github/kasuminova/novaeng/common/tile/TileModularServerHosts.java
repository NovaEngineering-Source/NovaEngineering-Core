package github.kasuminova.novaeng.common.tile;

import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.util.ResourceLocation;

public class TileModularServerHosts extends TileCustomController {

    public TileModularServerHosts() {
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
