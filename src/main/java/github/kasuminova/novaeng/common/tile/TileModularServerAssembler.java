package github.kasuminova.novaeng.common.tile;

import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileModularServerAssembler extends TileCustomController {

    IOInventory moduleInv;

    @Override
    public void doControllerTick() {

    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(moduleInv);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isWorking() {
        return controllerStatus.isCrafting();
    }

}
