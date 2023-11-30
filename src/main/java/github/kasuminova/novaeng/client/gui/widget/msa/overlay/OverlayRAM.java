package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotRAM;
import net.minecraft.util.ResourceLocation;

public class OverlayRAM extends SlotConditionTextureOverlay {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int TEX_X = 207;
    public static final int TEX_Y = 97;

    public static final int WIDTH = 48;
    public static final int HEIGHT = 3;

    public OverlayRAM(final SlotRAM slotRAM) {
        super(slotRAM);
        this.texLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

}
