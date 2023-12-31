package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class WidgetContainer extends DynamicWidget {
    protected static final ThreadLocal<LinkedList<Rectangle>> SCISSOR_STACK = ThreadLocal.withInitial(LinkedList::new);

    protected int absX;
    protected int absY;

    public static void enableScissor(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final int width, final int height) {
        final int guiLeft = (gui.width - gui.getXSize()) / 2;
        final int guiTop = (gui.height - gui.getYSize()) / 2;

        int offsetX = renderPos.posX();
        int offsetY = renderPos.posY();

        if (renderSize.isLimited()) {
            LinkedList<Rectangle> scissorStack = SCISSOR_STACK.get();

            ScaledResolution res = new ScaledResolution(gui.mc);
            int scissorWidth = renderSize.isWidthLimited() ? renderSize.width() : width;
            int scissorHeight = renderSize.isHeightLimited() ? renderSize.height() : height;

            Rectangle scissorFrame = new Rectangle(
                    (guiLeft + offsetX) * res.getScaleFactor(),
                    // y is left bottom...
                    gui.mc.displayHeight - ((guiTop + offsetY + scissorHeight) * res.getScaleFactor()),
                    scissorWidth * res.getScaleFactor(),
                    scissorHeight * res.getScaleFactor()
            );

            if (scissorStack.peekFirst() == null) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }
            GL11.glScissor(scissorFrame.x, scissorFrame.y, scissorFrame.width, scissorFrame.height);

            scissorStack.push(scissorFrame);
        }
    }

    public static void disableScissor(final RenderSize renderSize) {
        if (renderSize.isLimited()) {
            LinkedList<Rectangle> scissorStack = SCISSOR_STACK.get();
            scissorStack.pop();

            Rectangle prevScissorFrame = scissorStack.peekFirst();
            if (prevScissorFrame == null) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                GL11.glScissor(prevScissorFrame.x, prevScissorFrame.y, prevScissorFrame.width, prevScissorFrame.height);
            }
        }
    }

    @Override
    public final void preRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        enableScissor(gui, renderSize, renderPos, getWidth(), getHeight());

        try {
            preRenderInternal(gui, renderSize, renderPos, mousePos);
        } catch (Exception e) {
            SCISSOR_STACK.get().clear();
            NovaEngineeringCore.log.error("Error when rendering dynamic widgets!", e);
            throw e;
        } finally {
            disableScissor(renderSize);
        }
    }

    @Override
    public final void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        enableScissor(gui, renderSize, renderPos, getWidth(), getHeight());

        try {
            renderInternal(gui, renderSize, renderPos, mousePos);
        } catch (Exception e) {
            SCISSOR_STACK.get().clear();
            NovaEngineeringCore.log.error("Error when rendering dynamic widgets!", e);
            throw e;
        } finally {
            disableScissor(renderSize);
        }
    }

    @Override
    public final void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        enableScissor(gui, renderSize, renderPos, getWidth(), getHeight());

        try {
            postRenderInternal(gui, renderSize, renderPos, mousePos);
        } catch (Exception e) {
            SCISSOR_STACK.get().clear();
            NovaEngineeringCore.log.error("Error when rendering dynamic widgets!", e);
            throw e;
        } finally {
            disableScissor(renderSize);
        }
    }

    protected abstract void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos);

    protected abstract void renderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos);

    protected abstract void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos);

    public abstract List<DynamicWidget> getWidgets();

    public abstract WidgetContainer addWidget(DynamicWidget widget);

    public WidgetContainer addWidgets(DynamicWidget... widgets) {
        for (final DynamicWidget widget : widgets) {
            addWidget(widget);
        }
        return this;
    }

    public abstract WidgetContainer removeWidget(DynamicWidget widget);

    // GUI EventHandlers

    @Override
    public void update(final GuiContainer gui) {
        for (DynamicWidget widget : getWidgets()) {
            if (widget.isDisabled()) {
                continue;
            }
            widget.update(gui);
        }
    }

    @Override
    public void onGUIClosed(final GuiContainer gui) {
        getWidgets().forEach(widget -> widget.onGUIClosed(gui));
    }

    @Override
    public void initWidget(final GuiContainer gui) {
        getWidgets().forEach(widget -> widget.initWidget(gui));
    }

    @Override
    public abstract boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton);

    @Override
    public abstract boolean onMouseReleased(final MousePos mousePos, final RenderPos renderPos);

    @Override
    public abstract boolean onMouseDWheel(final MousePos mousePos, final RenderPos renderPos, final int wheel);

    @Override
    public abstract boolean onKeyTyped(final char typedChar, final int keyCode);

    // Tooltips

    @Override
    public abstract List<String> getHoverTooltips(final MousePos mousePos);

    // Getter Setters

    public int getAbsX() {
        return absX;
    }

    public WidgetContainer setAbsX(final int absX) {
        this.absX = absX;
        return this;
    }

    public int getAbsY() {
        return absY;
    }

    public WidgetContainer setAbsY(final int absY) {
        this.absY = absY;
        return this;
    }

}