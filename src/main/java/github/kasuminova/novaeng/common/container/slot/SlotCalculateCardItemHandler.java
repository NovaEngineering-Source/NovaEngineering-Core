package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.computer.module.base.ModuleCalculateCardBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.TileItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotCalculateCardItemHandler extends SlotConditionItemHandler {

    public SlotCalculateCardItemHandler(final int displayID, final int index, final TileItemHandler inventoryIn) {
        super(inventoryIn, index, displayID);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.calculate_card.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleCalculateCardBase<?>;
    }
}
