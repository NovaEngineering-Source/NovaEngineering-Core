package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.server.module.base.ModuleCapacitorBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.TileItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotCapacitorItemHandler extends SlotConditionItemHandler {

    public SlotCapacitorItemHandler(final int displayID, final int index, final TileItemHandler inventoryIn) {
        super(inventoryIn, index, displayID);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.capacitor.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleCapacitorBase;
    }
}
