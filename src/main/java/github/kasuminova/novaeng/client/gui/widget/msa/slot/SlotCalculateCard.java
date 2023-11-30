package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotCalculateCardItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SlotCalculateCard extends SlotAssembly<SlotCalculateCardItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_calculate_card.png");
    public static final int TEX_X = 104;
    public static final int TEX_Y = 0;

    public static final int UNAVAILABLE_TEX_X = 25;
    public static final int UNAVAILABLE_TEX_Y = 7;

    protected final int displayID;

    public SlotCalculateCard(final int displayID, final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.displayID = displayID;
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = UNAVAILABLE_TEX_X;
        this.unavailableTextureY = UNAVAILABLE_TEX_Y;
    }

    @Override
    protected SlotCalculateCardItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("calculate_card", slotID);
        return slot instanceof SlotCalculateCardItemHandler ? (SlotCalculateCardItemHandler) slot : null;
    }

    @Override
    public <SLOT extends SlotAssembly<?>> SlotCalculateCard dependsOn(final SLOT dependency) {
        return (SlotCalculateCard) super.dependsOn(dependency);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.calculate_card.name", displayID);
    }

}
