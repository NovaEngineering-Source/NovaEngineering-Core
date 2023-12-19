package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModuleRegistry;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ModuleRAMHeatRadiatorBase;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotRAMHeatRadiatorItemHandler extends SlotConditionItemHandler {

    public SlotRAMHeatRadiatorItemHandler(final int index, final ServerModuleInv inventoryIn) {
        super(inventoryIn, index, -1, 0, 0);
    }

    @Override
    public String getSlotDescription() {
        return "";
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleRAMHeatRadiatorBase;
    }

}
