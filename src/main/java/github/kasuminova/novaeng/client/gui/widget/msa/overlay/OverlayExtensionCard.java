package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotExtensionCard;
import net.minecraft.util.ResourceLocation;

public class OverlayExtensionCard extends TextureOverlay {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int TEX_X = 215;
    public static final int TEX_Y = 76;

    public static final int WIDTH = 7;
    public static final int HEIGHT = 14;

    protected final SlotExtensionCard slotExtensionCard;

    public OverlayExtensionCard(final SlotExtensionCard slotExtensionCard) {
        this.slotExtensionCard = slotExtensionCard;
        this.texLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public boolean isVisible() {
        return slotExtensionCard.isHovered();
    }

}
