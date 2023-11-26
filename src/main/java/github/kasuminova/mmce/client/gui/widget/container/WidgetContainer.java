package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public abstract class WidgetContainer extends DynamicWidget {
    protected int absX;
    protected int absY;

    public static void enableScissor(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int xSize, final int ySize) {
        int offsetX = renderOffset.getOffsetX();
        int offsetY = renderOffset.getOffsetY();

        if (renderSize.isLimited()) {
            ScaledResolution res = new ScaledResolution(gui.mc);
            Rectangle scissorFrame = new Rectangle(
                    offsetX * res.getScaleFactor(),
                    offsetY * res.getScaleFactor(),
                    (renderSize.isWidthLimited() ? xSize : renderSize.width()) * res.getScaleFactor(),
                    (renderSize.isHeightLimited() ? ySize : renderSize.height()) * res.getScaleFactor()
            );
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissorFrame.x, scissorFrame.y, scissorFrame.width, scissorFrame.height);
        }
    }

    public static void disableScissor(final RenderSize renderSize) {
        if (renderSize.isLimited()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public final void preRender(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        enableScissor(gui, renderSize, renderOffset, getXSize(), getYSize());

        preRenderInternal(gui, renderSize, renderOffset, mouseX, mouseY);

        disableScissor(renderSize);
    }

    @Override
    public final void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        enableScissor(gui, renderSize, renderOffset, getXSize(), getYSize());

        postRenderInternal(gui, renderSize, renderOffset, mouseX, mouseY);

        disableScissor(renderSize);
    }

    public abstract void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY);

    public abstract void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY);

    public abstract List<DynamicWidget> getWidgets();

    public abstract WidgetContainer addWidget(DynamicWidget widget);

    // GUI EventHandlers

    @Override
    public abstract void update(final GuiContainer gui);

    @Override
    public abstract boolean onMouseClicked(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int mouseButton);

    @Override
    public abstract boolean onMouseReleased(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset);

    @Override
    public abstract boolean onMouseDWheel(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int wheel);

    @Override
    public abstract boolean onKeyTyped(final char typedChar, final int keyCode);

    // Tooltips

    @Override
    public List<String> getHoverTooltips() {
        return super.getHoverTooltips();
    }

    // Getter Setters

    public int getAbsX() {
        return absX;
    }

    public void setAbsX(final int absX) {
        this.absX = absX;
    }

    public int getAbsY() {
        return absY;
    }

    public void setAbsY(final int absY) {
        this.absY = absY;
    }

}