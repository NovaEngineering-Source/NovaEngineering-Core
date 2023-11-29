package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class SlotDynamic extends DynamicWidget {

    protected ResourceLocation texLocation = null;
    protected ResourceLocation unavailableTexLocation = null;

    protected int textureX = 0;
    protected int textureY = 0;

    protected int unavailableTextureX = 0;
    protected int unavailableTextureY = 0;

    protected boolean hovered = false;

    public SlotDynamic() {
        this.width = 18;
        this.height = 18;
    }

    @Override
    public void update(final GuiContainer gui) {
        if (!isVisible()) {
            hovered = false;
        }
    }

    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (isVisible() && unavailableTexLocation != null && texLocation != null) {
            int texX;
            int texY;
            if (isAvailable()) {
                hovered = isMouseOver(mousePos);
                texX = textureX;
                texY = textureY;
                gui.mc.getTextureManager().bindTexture(texLocation);
            } else {
                texX = unavailableTextureX;
                texY = unavailableTextureY;
                gui.mc.getTextureManager().bindTexture(unavailableTexLocation);
            }

            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), texX, texY, width, height);
        }
    }

    public boolean isAvailable() {
        return true;
    }

    // Getters / Setters

    public ResourceLocation getTexLocation() {
        return texLocation;
    }

    public SlotDynamic setTexLocation(final ResourceLocation texLocation) {
        this.texLocation = texLocation;
        return this;
    }

    public ResourceLocation getUnavailableTexLocation() {
        return unavailableTexLocation;
    }

    public SlotDynamic setUnavailableTexLocation(final ResourceLocation unavailableTexLocation) {
        this.unavailableTexLocation = unavailableTexLocation;
        return this;
    }

    public int getTextureX() {
        return textureX;
    }

    public SlotDynamic setTextureX(final int textureX) {
        this.textureX = textureX;
        return this;
    }

    public int getTextureY() {
        return textureY;
    }

    public SlotDynamic setTextureY(final int textureY) {
        this.textureY = textureY;
        return this;
    }

    public int getUnavailableTextureX() {
        return unavailableTextureX;
    }

    public SlotDynamic setUnavailableTextureX(final int unavailableTextureX) {
        this.unavailableTextureX = unavailableTextureX;
        return this;
    }

    public int getUnavailableTextureY() {
        return unavailableTextureY;
    }

    public SlotDynamic setUnavailableTextureY(final int unavailableTextureY) {
        this.unavailableTextureY = unavailableTextureY;
        return this;
    }

    public boolean isHovered() {
        return hovered;
    }

    public SlotDynamic setHovered(final boolean hovered) {
        this.hovered = hovered;
        return this;
    }
}
