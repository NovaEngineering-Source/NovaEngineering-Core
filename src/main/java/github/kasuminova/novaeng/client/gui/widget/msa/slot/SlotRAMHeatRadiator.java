package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotRAMHeatRadiatorItemHandler;
import github.kasuminova.novaeng.common.util.RandomUtils;
import net.minecraft.util.ResourceLocation;

public class SlotRAMHeatRadiator extends SlotAssemblyDecor<SlotRAMHeatRadiatorItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");

    public static final int TEX_X = 139;
    public static final int TEX_Y = 56;

    public static final int UNAVAILABLE_TEX_X = 165;
    public static final int UNAVAILABLE_TEX_Y = 1;

    public static final int OVERLAY_X = 167;
    public static final int OVERLAY_Y = 37;
    public static final int OVERLAY_WIDTH = 18;
    public static final int OVERLAY_HEIGHT = 18;

    public static final int DECOR_OVERLAY_AMOUNT = 6;
    public static final int[] DECOR_OVERLAY_X = {186, 204, 222, 186, 203, 221};
    public static final int[] DECOR_OVERLAY_Y = {1, 1, 1, 19, 19, 19};
    public static final int DECOR_OVERLAY_WIDTH = 18;
    public static final int DECOR_OVERLAY_HEIGHT = 18;

    public SlotRAMHeatRadiator(final int slotID, final AssemblySlotManager slotManager) {
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
    protected SlotRAMHeatRadiatorItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("cpu", slotID);
        return slot instanceof SlotRAMHeatRadiatorItemHandler ? (SlotRAMHeatRadiatorItemHandler) slot : null;
    }
}
