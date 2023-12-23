package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import net.minecraft.client.gui.inventory.GuiContainer;

public class Button2State extends Button {

    protected int clickedTextureX = 0;
    protected int clickedTextureY = 0;

    protected boolean clicked = false;

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (isVisible() && textureLocation != null) {
            int texX;
            int texY;

            if (clicked) {
                texX = clickedTextureX;
                texY = clickedTextureY;
            } else if (isMouseOver(mousePos)) {
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
        if (isVisible()) {
            clicked = !clicked;
        }
        return super.onMouseClicked(mousePos, renderPos, mouseButton);
    }

    public Button setClickedTextureXY(final int clickedTextureX, final int clickedTextureY) {
        this.clickedTextureX = clickedTextureX;
        this.clickedTextureY = clickedTextureY;
        return this;
    }

    public int getClickedTextureX() {
        return clickedTextureX;
    }

    public Button2State setClickedTextureX(final int clickedTextureX) {
        this.clickedTextureX = clickedTextureX;
        return this;
    }

    public int getClickedTextureY() {
        return clickedTextureY;
    }

    public Button2State setClickedTextureY(final int clickedTextureY) {
        this.clickedTextureY = clickedTextureY;
        return this;
    }

    public boolean isClicked() {
        return clicked;
    }

    public Button2State setClicked(final boolean clicked) {
        this.clicked = clicked;
        return this;
    }
}
