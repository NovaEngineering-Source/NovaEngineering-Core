package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotCalculateCardExtItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.util.RandomUtils;
import net.minecraft.util.ResourceLocation;

public class SlotCalculateCardExtension extends SlotAssemblyDecor<SlotCalculateCardExtItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_calculate_card.png");

    public static final int TEX_X = 105;
    public static final int TEX_Y = 56;

    public static final int UNAVAILABLE_TEX_X = 105;
    public static final int UNAVAILABLE_TEX_Y = 1;

    public static final int OVERLAY_X = 161;
    public static final int OVERLAY_Y = 37;
    public static final int OVERLAY_WIDTH = 18;
    public static final int OVERLAY_HEIGHT = 18;

    public static final int DECOR_OVERLAY_AMOUNT = 6;
    public static final int[] DECOR_OVERLAY_X = {125, 143, 161, 125, 143, 161};
    public static final int[] DECOR_OVERLAY_Y = {1, 1, 1, 19, 19, 19};
    public static final int DECOR_OVERLAY_WIDTH = 18;
    public static final int DECOR_OVERLAY_HEIGHT = 18;

    public SlotCalculateCardExtension(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = TEX_X;
        this.unavailableTextureY = TEX_Y;

        this.overlayX = OVERLAY_X;
        this.overlayY = OVERLAY_Y;
        this.overlayWidth = OVERLAY_WIDTH;
        this.overlayHeight = OVERLAY_HEIGHT;

        int decorOverlayIndex = RandomUtils.nextInt(DECOR_OVERLAY_AMOUNT);
        this.decorOverlayX = DECOR_OVERLAY_X[decorOverlayIndex];
        this.decorOverlayY = DECOR_OVERLAY_Y[decorOverlayIndex];
        this.decorOverlayWidth = DECOR_OVERLAY_WIDTH;
        this.decorOverlayHeight = DECOR_OVERLAY_HEIGHT;
    }

    @Override
    protected SlotCalculateCardExtItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("calculate_card", slotID);
        return slot instanceof SlotCalculateCardExtItemHandler ? (SlotCalculateCardExtItemHandler) slot : null;
    }
}
