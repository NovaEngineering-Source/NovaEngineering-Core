package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCardExtension;
import net.minecraft.util.ResourceLocation;

public class OverlayCalculateCardExt extends SlotConditionTextureOverlay {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int TEX_X = 216;
    public static final int TEX_Y = 60;

    public static final int WIDTH = 40;
    public static final int HEIGHT = 10;

    public OverlayCalculateCardExt(final SlotCalculateCardExtension slotCalculateCardExt) {
        super(slotCalculateCardExt);
        this.texLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

}
