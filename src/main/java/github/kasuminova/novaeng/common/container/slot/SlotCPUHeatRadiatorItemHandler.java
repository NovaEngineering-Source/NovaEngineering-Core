package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.server.module.base.ModuleCPUHeatRadiatorBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.TileItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class SlotCPUHeatRadiatorItemHandler extends SlotConditionItemHandler {

    public SlotCPUHeatRadiatorItemHandler(final int displayID, final int index, final TileItemHandler inventoryIn) {
        super(inventoryIn, index, displayID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.cpu_heat_radiator.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleCPUHeatRadiatorBase;
    }
}
