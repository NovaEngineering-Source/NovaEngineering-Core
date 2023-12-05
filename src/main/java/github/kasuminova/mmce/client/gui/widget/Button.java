package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class Button extends DynamicWidget {

    protected ResourceLocation textureLocation = null;

    protected int textureX = 0;
    protected int textureY = 0;

    protected int hoveredTextureX = 0;
    protected int hoveredTextureY = 0;

    protected Consumer<Button> onClickedListener = null;

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (isVisible() && textureLocation != null) {
            int texX;
            int texY;
            if (isMouseOver(mousePos)) {
                texX = hoveredTextureX;
                texY = hoveredTextureY;
            } else {
                texX = textureX;
                texY = textureY;
            }

            gui.mc.getTextureManager().bindTexture(textureLocation);

            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), texX, texY, width, height);
        }
    }

    @Override
    public boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        if (isVisible() && onClickedListener != null) {
            onClickedListener.accept(this);
            return true;
        }
        return false;
    }

    // Getter Setter

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public Button setTextureLocation(final ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
        return this;
    }

    public int getTextureX() {
        return textureX;
    }

    public Button setTextureX(final int textureX) {
        this.textureX = textureX;
        return this;
    }

    public int getTextureY() {
        return textureY;
    }

    public Button setTextureY(final int textureY) {
        this.textureY = textureY;
        return this;
    }

    public Button setTextureXY(final int textureX, final int textureY) {
        this.textureX = textureX;
        this.textureY = textureY;
        return this;
    }

    public int getHoveredTextureX() {
        return hoveredTextureX;
    }

    public Button setHoveredTextureX(final int hoveredTextureX) {
        this.hoveredTextureX = hoveredTextureX;
        return this;
    }

    public int getHoveredTextureY() {
        return hoveredTextureY;
    }

    public Button setHoveredTextureY(final int hoveredTextureY) {
        this.hoveredTextureY = hoveredTextureY;
        return this;
    }

    public Button setHoveredTextureXY(final int hoveredTextureX, final int hoveredTextureY) {
        this.hoveredTextureX = hoveredTextureX;
        this.hoveredTextureY = hoveredTextureY;
        return this;
    }

    public Consumer<Button> getOnClickedListener() {
        return onClickedListener;
    }

    public Button setOnClickedListener(final Consumer<Button> onClickedListener) {
        this.onClickedListener = onClickedListener;
        return this;
    }

}
