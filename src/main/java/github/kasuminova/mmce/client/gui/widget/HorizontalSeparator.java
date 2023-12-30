package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;

public class HorizontalSeparator extends DynamicWidget {
    protected int color = 0xFFFFFFFF;

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        GuiContainer.drawRect(renderPos.posX(), renderPos.posY(), renderPos.posX() + width, renderPos.posY() + height, color);
    }

    public int getColor() {
        return color;
    }

    public HorizontalSeparator setColor(final int color) {
        this.color = color;
        return this;
    }
}
