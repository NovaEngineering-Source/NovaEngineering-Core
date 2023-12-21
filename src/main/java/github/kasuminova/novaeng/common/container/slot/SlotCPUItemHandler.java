package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ModuleCPUBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class SlotCPUItemHandler extends SlotConditionItemHandler {

    public SlotCPUItemHandler(final int displayID, final int index, final ServerModuleInv inventoryIn) {
        super(inventoryIn, index, displayID, 0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.cpu.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleCPUBase;
    }

}
