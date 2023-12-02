package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRegistry;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ModuleCalculateCardExtBase;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotCalculateCardExtItemHandler extends SlotConditionItemHandler {

    public SlotCalculateCardExtItemHandler(final int displayID, final int index, final ServerModuleInv inventoryIn) {
        super(inventoryIn, index, displayID, 0, 0);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.calculate_card_ext.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ModuleRegistry.getModule(stack) instanceof ModuleCalculateCardExtBase;
    }
}
