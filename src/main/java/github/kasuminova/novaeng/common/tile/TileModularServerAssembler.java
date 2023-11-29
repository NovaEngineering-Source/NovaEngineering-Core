package github.kasuminova.novaeng.common.tile;

import github.kasuminova.novaeng.common.hypernet.proc.server.ServerInvProvider;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileModularServerAssembler extends TileCustomController implements ServerInvProvider {

    private IOInventory serverInv;
    private IOInventory assemblyCPUInv;
    private IOInventory assemblyCalculateCardInv;
    private IOInventory assemblyExtensionInv;
    private IOInventory assemblyHeatRadiatorInv;
    private IOInventory assemblyPowerInv;

    @Override
    public void doControllerTick() {

    }

    public void writeAssemblyDataToServer(final ItemStack stack) {

    }

    @Override
    public IOInventory getInvByName(final String name) {
        return switch (name) {
            case "server" -> serverInv;
            case "cpu" -> assemblyCPUInv;
            case "calculate_card" -> assemblyCalculateCardInv;
            case "heat_radiator" -> assemblyHeatRadiatorInv;
            case "power" -> assemblyPowerInv;
            default -> null;
        };
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isWorking() {
        return controllerStatus.isCrafting();
    }

}
