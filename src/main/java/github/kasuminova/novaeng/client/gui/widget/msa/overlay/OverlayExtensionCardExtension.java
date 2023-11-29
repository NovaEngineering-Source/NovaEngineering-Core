package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotExtensionCardExtension;
import net.minecraft.util.ResourceLocation;

public class OverlayExtensionCardExtension extends TextureOverlay {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int TEX_X = 223;
    public static final int TEX_Y = 77;

    public static final int WIDTH = 4;
    public static final int HEIGHT = 12;

    protected final SlotExtensionCardExtension slotExtensionCardExtension;

    public OverlayExtensionCardExtension(final SlotExtensionCardExtension slotExtensionCardExtension) {
        this.slotExtensionCardExtension = slotExtensionCardExtension;
        this.texLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public boolean isVisible() {
        return slotExtensionCardExtension.isHovered();
    }

}
