package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotCalculateCardExtItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SlotCalculateCardExtension extends SlotAssembly<SlotCalculateCardExtItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_calculate_card.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 7;

    protected final int displayID;

    public SlotCalculateCardExtension(final int displayID, final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.displayID = displayID;
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = TEX_X;
        this.unavailableTextureY = TEX_Y;
    }

    @Override
    protected SlotCalculateCardExtItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("calculate_card", slotID);
        return slot instanceof SlotCalculateCardExtItemHandler ? (SlotCalculateCardExtItemHandler) slot : null;
    }

    @Override
    public boolean isAvailable() {
        return slot != null;
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.calculate_card_ext.name", displayID);
    }
}
