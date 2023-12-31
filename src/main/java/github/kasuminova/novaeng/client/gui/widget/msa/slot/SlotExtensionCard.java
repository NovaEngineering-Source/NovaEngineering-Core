package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotExtensionCardItemHandler;
import github.kasuminova.novaeng.common.util.RandomUtils;
import net.minecraft.util.ResourceLocation;

public class SlotExtensionCard extends SlotAssemblyDecor<SlotExtensionCardItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_extension.png");
    public static final int TEX_X = 121;
    public static final int TEX_Y = 38;

    public static final int UNAVAILABLE_TEX_X = 147;
    public static final int UNAVAILABLE_TEX_Y = 1;

    public static final int OVERLAY_X = 221;
    public static final int OVERLAY_Y = 19;
    public static final int OVERLAY_WIDTH = 18;
    public static final int OVERLAY_HEIGHT = 18;

    public static final int DECOR_OVERLAY_AMOUNT = 6;
    public static final int[] DECOR_OVERLAY_X = {167, 185, 203, 167, 185, 203};
    public static final int[] DECOR_OVERLAY_Y = {1, 1, 1, 19, 19, 19};
    public static final int DECOR_OVERLAY_WIDTH = 18;
    public static final int DECOR_OVERLAY_HEIGHT = 18;

    public SlotExtensionCard(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = UNAVAILABLE_TEX_X;
        this.unavailableTextureY = UNAVAILABLE_TEX_Y;

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
    protected SlotExtensionCardItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager == null ? null : slotManager.getSlot("extension", slotID);
        return slot instanceof SlotExtensionCardItemHandler ? (SlotExtensionCardItemHandler) slot : null;
    }
}
