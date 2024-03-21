package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.hypernet.server.module.base.ModuleRAMBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.TileItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class SlotRAMItemHandler extends SlotConditionItemHandler {

    public SlotRAMItemHandler(final int displayID, final int index, final TileItemHandler inventoryIn) {
        super(inventoryIn, index, displayID);
    }

    public SlotRAMItemHandler dependsOn(SlotConditionItemHandler dependency) {
        super.dependsOn(dependency);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.ram.name", displayID);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack stack) {
        return ServerModuleRegistry.getModule(stack) instanceof ModuleRAMBase;
    }

}
