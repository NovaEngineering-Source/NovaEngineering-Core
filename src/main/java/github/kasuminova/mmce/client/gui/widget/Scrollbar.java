package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class Scrollbar extends DynamicWidget {
    public static final ResourceLocation DEFAULT_RES = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/guiterminalelement.png");

    public static final int DEFAULT_SCROLL_WIDTH = 6;
    public static final int DEFAULT_SCROLL_HEIGHT = 27;

    public static final int DEFAULT_TEXTURE_X = 0;
    public static final int DEFAULT_TEXTURE_Y = 22;

    public static final int DEFAULT_TEXTURE_OFFSET_X = 6;
    public static final int DEFAULT_TEXTURE_OFFSET_Y = 0;

    public static final int DEFAULT_SCROLL_UNIT = 1;

    protected ResourceLocation textureLocation = DEFAULT_RES;

    protected int scrollWidth = DEFAULT_SCROLL_WIDTH;
    protected int scrollHeight = DEFAULT_SCROLL_HEIGHT;

    protected int textureX = DEFAULT_TEXTURE_X;
    protected int textureY = DEFAULT_TEXTURE_Y;

    protected int disabledTextureOffsetX = DEFAULT_TEXTURE_OFFSET_X;
    protected int disabledTextureOffsetY = DEFAULT_TEXTURE_OFFSET_Y;

    protected int maxScroll = 0;
    protected int minScroll = 0;
    protected int currentScroll = 0;

    protected int scrollUnit = DEFAULT_SCROLL_UNIT;

    protected boolean mouseDown = false;

    public Scrollbar() {
        this.width = scrollWidth;
        this.height = scrollHeight * 2;
    }

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (mouseDown) {
            handleMouseDragMove(mousePos);
        }

        gui.mc.getTextureManager().bindTexture(textureLocation);
        int offsetX = renderPos.posX();
        int offsetY = renderPos.posY();
        int height = renderSize.isHeightLimited() ? renderSize.height() : this.height;

        if (this.getRange() == 0) {
            gui.drawTexturedModalRect(offsetX, offsetY,
                    this.textureX + this.disabledTextureOffsetX,
                    this.textureY + this.disabledTextureOffsetY,
                    this.scrollWidth, this.scrollHeight
            );
        } else {
            offsetY += (this.currentScroll - this.minScroll) * (height - this.scrollHeight) / this.getRange();
            gui.drawTexturedModalRect(offsetX, offsetY, this.textureX, this.textureY, this.scrollWidth, this.scrollHeight);
        }
    }

    @Override
    public boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        if (this.getRange() == 0) {
            return false;
        }
        mouseDown = true;
        return true;
    }

    protected void handleMouseDragMove(final MousePos mousePos) {
        float clickedPercent = Math.min(Math.max((float) mousePos.mouseY() / this.height, 0), 1F);
        int scroll = Math.round((float) getRange() * clickedPercent);
        setCurrentScroll(scroll + this.minScroll);
    }

    @Override
    public boolean onMouseReleased(final MousePos mousePos, final RenderPos renderPos) {
        mouseDown = false;
        return false;
    }

    @Override
    public boolean onMouseDWheel(final MousePos mousePos, final RenderPos renderPos, final int dWheel) {
        int wheel = Math.max(Math.min(-dWheel, 1), -1);
        setCurrentScroll(this.currentScroll + (wheel * this.scrollUnit));

        return true;
    }

    // Scroll Width / Height

    public int getScrollWidth() {
        return scrollWidth;
    }

    public Scrollbar setScrollWidth(final int scrollWidth) {
        this.scrollWidth = scrollWidth;
        return this;
    }

    public int getScrollHeight() {
        return scrollHeight;
    }

    public Scrollbar setScrollHeight(final int scrollHeight) {
        this.scrollHeight = scrollHeight;
        return this;
    }

    // Scroll Texture Enabled/Disabled X/Y

    public int getTextureX() {
        return textureX;
    }

    public Scrollbar setTextureX(final int textureX) {
        this.textureX = textureX;
        return this;
    }

    public int getTextureY() {
        return textureY;
    }

    public Scrollbar setTextureY(final int textureY) {
        this.textureY = textureY;
        return this;
    }

    public int getDisabledTextureOffsetX() {
        return disabledTextureOffsetX;
    }

    public Scrollbar setDisabledTextureOffsetX(final int disabledTextureOffsetX) {
        this.disabledTextureOffsetX = disabledTextureOffsetX;
        return this;
    }

    public int getDisabledTextureOffsetY() {
        return disabledTextureOffsetY;
    }

    public Scrollbar setDisabledTextureOffsetY(final int disabledTextureOffsetY) {
        this.disabledTextureOffsetY = disabledTextureOffsetY;
        return this;
    }

    // Scroll Range

    public int getMaxScroll() {
        return maxScroll;
    }

    public int getMinScroll() {
        return minScroll;
    }

    public int getCurrentScroll() {
        return currentScroll;
    }

    public Scrollbar setCurrentScroll(final int currentScroll) {
        this.currentScroll = Math.max(Math.min(currentScroll, this.maxScroll), this.minScroll);
        return this;
    }

    public int getRange() {
        return this.maxScroll - this.minScroll;
    }

    public Scrollbar setRange(final int min, final int max) {
        this.minScroll = min;
        this.maxScroll = max;

        if (this.minScroll > this.maxScroll) {
            this.maxScroll = this.minScroll;
        }

        setCurrentScroll(this.currentScroll);
        return this;
    }

    // Scroll Unit

    public int getScrollUnit() {
        return scrollUnit;
    }

    public Scrollbar setScrollUnit(final int scrollUnit) {
        this.scrollUnit = Math.max(scrollUnit, 1);
        return this;
    }
}
