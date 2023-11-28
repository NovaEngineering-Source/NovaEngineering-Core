package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Row extends WidgetContainer {
    protected final List<DynamicWidget> widgets = new ArrayList<>();

    protected boolean upAligned = true;
    protected boolean downAligned = false;

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        int x = 0;

        int height = getHeight();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, height, x);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            widget.preRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            x += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        int x = 0;

        int height = getHeight();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, height, x);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            widget.postRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            x += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }
    }

    @Override
    public List<DynamicWidget> getWidgets() {
        return widgets;
    }

    @Override
    public Row addWidget(final DynamicWidget widget) {
        widgets.add(widget);
        return this;
    }

    // GUI EventHandlers

    @Override
    public void update(final GuiContainer gui) {
        widgets.forEach(widget -> widget.update(gui));
    }

    @Override
    public boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        int x = 0;

        int height = getHeight();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, height, x);
            if (widgetRenderPos == null) {
                continue;
            }
            int offsetX = widgetRenderPos.posX();
            int offsetY = widgetRenderPos.posY();

            if (widget.isMouseOver(offsetX, offsetY, mousePos.mouseX(), mousePos.mouseY())) {
                RenderPos absRenderPos = widgetRenderPos.add(renderPos);
                if (widget.onMouseClicked(mousePos.relativeTo(widgetRenderPos), absRenderPos, mouseButton)) {
                    return true;
                }
            }
            x += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final MousePos mousePos, final RenderPos renderPos) {
        int x = 0;

        int height = getHeight();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, height, x);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            if (widget.onMouseReleased(mousePos.relativeTo(widgetRenderPos), absRenderPos)) {
                return true;
            }
            x += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }
        return false;
    }

    @Override
    public boolean onMouseDWheel(final MousePos mousePos, final RenderPos renderPos, final int wheel) {
        int x = 0;

        int height = getHeight();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, height, x);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            if (widget.onMouseDWheel(mousePos.relativeTo(widgetRenderPos), absRenderPos, wheel)) {
                return true;
            }
            x += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }
        return false;
    }

    @Override
    public boolean onKeyTyped(final char typedChar, final int keyCode) {
        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            if (widget.onKeyTyped(typedChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    // CustomEventHandlers

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            if (widget.onGuiEvent(event)) {
                return true;
            }
        }
        return false;
    }

    // Utils

    public RenderPos getWidgetRenderOffset(DynamicWidget widget, int height, int x) {
        int xOffset;
        int yOffset;

        if (isCenterAligned()) {
            xOffset = x + widget.getMarginLeft();
            yOffset = (height - (widget.getMarginUp() + widget.getHeight() + widget.getMarginDown())) / 2;
        } else if (upAligned) {
            xOffset = x + widget.getMarginLeft();
            yOffset = widget.getMarginUp();
        } else if (downAligned) {
            xOffset = x + widget.getMarginLeft();
            yOffset = height - (widget.getHeight() + widget.getMarginDown());
        } else {
            // Where does it align?
            return null;
        }

        return new RenderPos(xOffset, yOffset);
    }

    // X/Y Size

    @Override
    public int getWidth() {
        int width = 0;
        for (final DynamicWidget widget : widgets) {
            width += widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
        }
        return width;
    }

    @Override
    public DynamicWidget setWidth(final int width) {
        // It's dynamic, so ignore it.
        return this;
    }

    @Override
    public int getHeight() {
        int maxY = 0;
        for (final DynamicWidget widget : widgets) {
            int height = widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
            if (height > maxY) {
                maxY = height;
            }
        }
        return maxY;
    }

    @Override
    public DynamicWidget setHeight(final int height) {
        // It's dynamic, so ignore it.
        return this;
    }

    // Align

    public boolean isUpAligned() {
        return upAligned;
    }

    public Row setUpAligned(final boolean upAligned) {
        this.downAligned = !upAligned;
        this.upAligned = upAligned;
        return this;
    }

    public boolean isDownAligned() {
        return downAligned;
    }

    public Row setDownAligned(final boolean downAligned) {
        this.upAligned = !downAligned;
        this.downAligned = downAligned;
        return this;
    }

    public Row setCenterAligned(final boolean centerAligned) {
        if (centerAligned) {
            this.upAligned = true;
            this.downAligned = true;
            return this;
        }
        // Default setting is up aligned.
        this.upAligned = true;
        this.downAligned = false;
        return this;
    }

    public boolean isCenterAligned() {
        return this.upAligned && this.downAligned;
    }
}
